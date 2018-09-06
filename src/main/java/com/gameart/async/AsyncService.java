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
    private static AsyncConfig config;
    private static final ConcurrentLinkedQueue<AsyncObject> QUERY_QUEUE = new ConcurrentLinkedQueue<>();
    private static final ConcurrentLinkedQueue<AsyncObject> UPDATE_QUEUE = new ConcurrentLinkedQueue<>();

    public static void start(AsyncConfig config) throws IOException, ConfictMethodException, IllegalMethodException, IllegalClassException,IllegalArgumentException {
        AsyncService.config = config;
        check(config);
        DBManager.getInstance().start();
        MapperManager.getInstance().init();
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.execute(new QueryWorker(QUERY_QUEUE, config.queryTps));
        executorService.execute(new UpdateWorker(UPDATE_QUEUE, config.updateTps));
    }

    private static void check(AsyncConfig config) throws IllegalArgumentException{
        if(config.queryTps<=0){
            throw new IllegalArgumentException("AsyncConfig queryTps must be greater than 0");
        }
        if(config.updateTps <= 0){
            throw new IllegalArgumentException("AsyncConfig updateTps must be greater than 0");
        }
        if(config.asyncQueueMax <= 0){
            throw new IllegalArgumentException("AsyncConfig asyncQueueMax must be greater than 0");
        }
        if(config.queueFullListener == null){
            throw new IllegalArgumentException("AsyncConfig queueFullListener can not be null.");
        }

    }

    public static void commitAsyncTask(Class mapperClazz, String methodId, ParamBuilder builder) {
        addAsyncObject(new AsyncObject(mapperClazz, methodId, builder));
    }

    public static void commitAsyncTask(Class mapperClazz, String methodId, ParamBuilder builder, IAsyncListener listener) {
        addAsyncObject(new AsyncObject(mapperClazz, methodId, builder, listener));
    }


    private static void addAsyncObject(AsyncObject asyncObject) {
        String methodId = asyncObject.getMethodId();
        Class mapperClazz = asyncObject.getMapperClazz();
        AsyncType asyncType = MapperManager.getInstance().getAsyncType(mapperClazz, methodId);
        if (asyncType == null) {
            LOGGER.error("add async object error ,asyncType is null.");
            return;
        }
        if (asyncType == AsyncType.SELECT) {
            if (AsyncService.config.asyncQueueMax <= QUERY_QUEUE.size()) {
                AsyncService.config.queueFullListener.onFull(asyncObject);
            } else {
                QUERY_QUEUE.add(asyncObject);
            }
        } else if (asyncType == AsyncType.INSERT || asyncType == AsyncType.UPDATE) {
            if(AsyncService.config.asyncQueueMax <= UPDATE_QUEUE.size()){
                AsyncService.config.queueFullListener.onFull(asyncObject);
            }else {
                UPDATE_QUEUE.add(asyncObject);
            }
        } else {
            LOGGER.error("add async object error ,can not found this type = {}", asyncType);
        }
    }


    /****
     *
     * @param mapperClazz
     * @param methodId
     * @param builder 参数，可以通过{@link ParamBuilder#create()}后，调用addXX方法创建
     * @param listener
     * @return
     * @throws IllegalMethodException
     */
    private static void execute(Class mapperClazz, String methodId, ParamBuilder builder, IAsyncListener listener) throws IllegalMethodException {
        Method asyncMethod = MapperManager.getInstance().getAsyncMethod(mapperClazz, methodId);
        if (asyncMethod == null) {
            LOGGER.error(" can not found async method  from [{}]  when  methodId =[{}] ", mapperClazz, methodId);
            return;
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
            return;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            LOGGER.error(String.format("async method[%s] invoke error ,method param={%s}", asyncMethod, builder), e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return;
    }

    private static void execute(AsyncObject asyncObject) throws IllegalMethodException {
        execute(asyncObject.getMapperClazz(), asyncObject.getMethodId(), asyncObject.getBuilder(), asyncObject.getListener());
    }


    static class QueryWorker extends AbstractAsyncWorker<AsyncObject> {
        private int tps;

        public QueryWorker(ConcurrentLinkedQueue<AsyncObject> queue, int tps) {
            super(queue);
            this.tps = tps;
        }

        @Override
        public String getName() {
            return "Query";
        }

        @Override
        public int getTPS() {
            return tps;
        }

        @Override
        public void execute(AsyncObject o) {
            try {
                AsyncService.execute(o);
            } catch (IllegalMethodException e) {
                e.printStackTrace();
            }
        }
    }

    static class UpdateWorker extends AbstractAsyncWorker<AsyncObject> {
        private int tps;

        public UpdateWorker(ConcurrentLinkedQueue<AsyncObject> queue, int tps) {
            super(queue);
            this.tps = tps;
        }

        @Override
        public String getName() {
            return "Update";
        }

        @Override
        public int getTPS() {
            return tps;
        }

        @Override
        public void execute(AsyncObject o) {
            try {
                AsyncService.execute(o);
            } catch (IllegalMethodException e) {
                e.printStackTrace();
            }
        }
    }
}
