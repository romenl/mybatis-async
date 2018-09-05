package com.gameart.async;

import com.gameart.async.annotations.AsyncType;
import com.gameart.async.api.IAsyncListener;
import com.gameart.async.core.AsyncObject;
import com.gameart.async.core.ParamBuilder;
import com.gameart.async.exception.ConfictMethodException;
import com.gameart.async.exception.IllegalClassException;
import com.gameart.async.exception.IllegalMethodException;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/***
 *@author JackLei
 *@Date 下午 5:54 2018/8/31
 ***/
public class AsyncService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncService.class);

    private static final ConcurrentLinkedQueue<AsyncObject> QUERY_QUEUE = new ConcurrentLinkedQueue<>();
    private static final ConcurrentLinkedQueue<AsyncObject> UPDATE_QUEUE = new ConcurrentLinkedQueue<>();

    public static void start() throws IOException, ConfictMethodException, IllegalMethodException, IllegalClassException {
        DBManager.getInstance().start();
        MapperManager.getInstance().init();
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.execute(new QueryWorker(QUERY_QUEUE));
        executorService.execute(new UpdateWorker(UPDATE_QUEUE));
    }


    public static void commitAsyncTask(AsyncObject asyncObject) {
        addAsyncObject(asyncObject);
    }

    public static void commitAsyncTask(AsyncType type, Class mapperClazz, String methodId, ParamBuilder builder) {
        addAsyncObject(new AsyncObject(type, mapperClazz, methodId, builder));
    }

    public static void commitAsyncTask(AsyncType type, Class mapperClazz, String methodId, ParamBuilder builder, IAsyncListener listener) {
        addAsyncObject(new AsyncObject(type, mapperClazz, methodId, builder, listener));
    }


    private static void addAsyncObject(AsyncObject asyncObject) {
        if (asyncObject.getType() == AsyncType.SELECT) {
            QUERY_QUEUE.add(asyncObject);
        } else if(asyncObject.getType() == AsyncType.INSERT || asyncObject.getType() == AsyncType.UPDATE ){
            UPDATE_QUEUE.add(asyncObject);
        }else{
            LOGGER.error("add async object error ,can not found this type = {}",asyncObject.getType());
        }
    }


    /****
     *
     * @param type
     * @param mapperClazz
     * @param methodId
     * @param builder 参数，可以通过{@link ParamBuilder#create()}后，调用addXX方法创建
     * @param listener
     * @return
     * @throws IllegalMethodException
     */
    protected static Object execute(AsyncType type, Class mapperClazz, String methodId, ParamBuilder builder, IAsyncListener listener) throws IllegalMethodException {
        Method asyncMethod = MapperManager.getInstance().getAsyncMethod(mapperClazz, type, methodId);
        if (asyncMethod == null) {
            LOGGER.error(" can not found async method  from [{}]  when  type = [{}] ,methodId =[{}] ", mapperClazz,type, methodId);
            return null;
        }
        SqlSession session = null;
        try {
            session = DBManager.getInstance().getFactory().openSession();
            Object mapper = session.getMapper(mapperClazz);
            Object invoke = asyncMethod.invoke(mapper, builder.build());
            session.commit();
            if (listener != null) {
                listener.callBack(invoke);
            }
            return invoke;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }catch(IllegalArgumentException e){
           LOGGER.error(String.format("async method[%s] invoke error ,method param={%s}",asyncMethod,builder),e);
        }finally {
            if(session!=null){
                session.close();
            }
        }
        return null;
    }

    protected static Object execute(AsyncType type, Class doClazz, String methodId, ParamBuilder dto) throws IllegalMethodException {
        return execute(type, doClazz, methodId, dto, null);
    }

    protected static Object execute(AsyncObject asyncObject) throws IllegalMethodException {
        return execute(asyncObject.getType(),asyncObject.getMapperClazz(),asyncObject.getMethodId(),asyncObject.getBuilder(),asyncObject.getListener());
    }


    static class QueryWorker extends AbstractAsyncWorker {

        public QueryWorker(ConcurrentLinkedQueue<AsyncObject> queue) {
            super(queue);
        }

        @Override
        public String getName() {
            return "Query";
        }

        @Override
        public int getTPS() {
            return 3000;
        }

        @Override
        public void execute(Object o) {
            try {
                AsyncService.execute((AsyncObject)o);
            } catch (IllegalMethodException e) {
                e.printStackTrace();
            }
        }
    }

    static class UpdateWorker extends AbstractAsyncWorker {

        public UpdateWorker(ConcurrentLinkedQueue<AsyncObject> queue) {
            super(queue);
        }

        @Override
        public String getName() {
            return "Update";
        }

        @Override
        public int getTPS() {
            return 2000;
        }

        @Override
        public void execute(Object o) {
            try {
                AsyncService.execute((AsyncObject)o);
            } catch (IllegalMethodException e) {
                e.printStackTrace();
            }
        }
    }
}
