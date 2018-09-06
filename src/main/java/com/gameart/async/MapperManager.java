package com.gameart.async;

import com.gameart.async.annotations.AsyncType;
import com.gameart.async.core.AsyncMethodTables;
import com.gameart.async.exception.ConfictMethodException;
import com.gameart.async.exception.IllegalClassException;
import com.gameart.async.exception.IllegalMethodException;
import org.apache.ibatis.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;

/***
 *@author JackLei
 *@Date 下午 8:16 2018/8/27
 ***/
public class MapperManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(MapperManager.class);
    private static final Map<Class, AsyncMethodTables> MAPPER_TO_ASYNC_METHODS = new HashMap<>();
    private static final MapperManager instance = new MapperManager();

    private MapperManager() {
    }

    public static MapperManager getInstance(){
        return instance;
    }

    public void init() throws ConfictMethodException, IllegalMethodException, IllegalClassException {
        Configuration configuration = DBManager.getInstance().getConfiguration();
        Collection<Class<?>> mappers = configuration.getMapperRegistry().getMappers();
        initMapperMethod(mappers);
    }

    public Method getAsyncMethod(Class mapperClass, AsyncType type, String methodId) {
        AsyncMethodTables asyncMethodTables = MAPPER_TO_ASYNC_METHODS.get(mapperClass);
        if(asyncMethodTables == null){
            LOGGER.error( " async method not found ,asyncMethodTables is null. doClass = {} ",mapperClass);
            return null;
        }
        return asyncMethodTables.getMethod(type,methodId);
    }

    public Method getAsyncMethod(Class mapperClass, String methodId) {
        AsyncMethodTables asyncMethodTables = MAPPER_TO_ASYNC_METHODS.get(mapperClass);
        if(asyncMethodTables == null){
            LOGGER.error( " async method not found ,asyncMethodTables is null. doClass = {} ",mapperClass);
            return null;
        }
        return asyncMethodTables.getMethod(methodId);
    }

    public AsyncType getAsyncType(Class mapperClass,String methodId){
        AsyncMethodTables asyncMethodTables = MAPPER_TO_ASYNC_METHODS.get(mapperClass);
        if(asyncMethodTables == null){
            LOGGER.error( " async method not found ,asyncMethodTables is null. doClass = {} ",mapperClass);
            return null;
        }
        return asyncMethodTables.getAsyncType(methodId);
    }

    private void initMapperMethod(Collection<Class<?>> mappers) throws ConfictMethodException, IllegalMethodException, IllegalClassException {
        for (Class mapperClazz : mappers) {
            AsyncMethodTables asyncMethodTables = MAPPER_TO_ASYNC_METHODS.get(mapperClazz);
            if (asyncMethodTables == null) {
                asyncMethodTables = new AsyncMethodTables(mapperClazz);
                MAPPER_TO_ASYNC_METHODS.put(mapperClazz, asyncMethodTables);
            }
            Method[] methods = mapperClazz.getMethods();
            for (Method method : methods) {
                asyncMethodTables.addMethod(method);
            }
        }

    }


}
