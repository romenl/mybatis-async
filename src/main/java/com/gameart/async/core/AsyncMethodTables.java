package com.gameart.async.core;

import com.gameart.async.annotations.AsyncMethod;
import com.gameart.async.exception.ConfictMethodException;
import com.gameart.async.exception.IllegalClassException;
import com.gameart.async.exception.IllegalMethodException;
import com.gameart.async.annotations.AsyncMapper;
import com.gameart.async.annotations.AsyncType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;

/***方法表
 *@author JackLei
 *@Date 下午 3:30 2018/8/31
 ***/
public class AsyncMethodTables {
    private Class mapperClazz;
    private HashMap<AsyncType, AsyncMethodTable> methods = new HashMap<>();
    private HashMap<String, Method> methodId2Methods = new HashMap<>();
    private HashMap<String,AsyncType> methodId2AsyncType = new HashMap<>();
    private HashSet<String> methodIdSet = new HashSet<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncMethodTable.class);
    private static final int MAX_METHOD_ID_LEN = 20;

    public AsyncMethodTables(Class mapperClazz) {
        this.mapperClazz = mapperClazz;
    }

    public void addMethod(Method method) throws ConfictMethodException, IllegalMethodException, IllegalClassException {
        if (!mapperClazz.isAnnotationPresent(AsyncMapper.class)) {
            throw new IllegalClassException(mapperClazz + ", must be configured AsyncMapper annotation.");
        }
        boolean annotationPresent = method.isAnnotationPresent(AsyncMethod.class);
        if (!annotationPresent) {
            return;
        }
        AsyncMethod annotation = method.getAnnotation(AsyncMethod.class);
        AsyncType type = annotation.type();
        String methodId = annotation.id();
        if (methodId.getBytes().length > MAX_METHOD_ID_LEN) {
            throw new IllegalMethodException(String.format("mapperClazz[%s]->[%s],id[%s] is too long", mapperClazz, method, methodId));
        }
        if (!validMethodIdLen(methodId)) {
            throw new IllegalMethodException(String.format("mapperClazz[%s]->[%s],id[%s] must be letters", mapperClazz, method, methodId));
        }

        switch (type) {
            case INSERT:
            case UPDATE:
                if (method.getParameterCount() == 0) {
                    throw new IllegalMethodException(method.toString() + " ,parameters must be greater than 1.");
                }
                break;
            case SELECT:

                break;
        }

        AsyncMethodTable asyncMethodTable = methods.get(type);
        if (asyncMethodTable == null) {
            asyncMethodTable = new AsyncMethodTable();
            methods.put(type, asyncMethodTable);
        }

        if (methodIdSet.contains(methodId)) {
            throw new IllegalMethodException(method.toString() + " ,methodId [" + methodId + "] dupicate.");
        }

        asyncMethodTable.putMethod(methodId, method);
        methodId2Methods.put(methodId,method);
        methodId2AsyncType.put(methodId,type);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("[{} put async method , ref = {} , method = {}]", mapperClazz, methodId, method);
        }
    }

    public Method getMethod(AsyncType type, String methodId) {
        AsyncMethodTable asyncMethodTable = methods.get(type);
        if (asyncMethodTable == null) {
            return null;
        }
        return asyncMethodTable.getMethod(methodId);
    }

    public Method getMethod(String methodId){
        return methodId2Methods.get(methodId);
    }

    public AsyncType getAsyncType(String methodId){
        return methodId2AsyncType.get(methodId);
    }

    public boolean validMethodIdLen(String methodId) {
        char[] chars = methodId.toCharArray();
        for (char c : chars) {
            if (!Character.isLetter(c)) {
                return false;
            }
        }
        return true;
    }
}
