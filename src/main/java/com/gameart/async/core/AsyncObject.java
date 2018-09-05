package com.gameart.async.core;

import com.gameart.async.annotations.AsyncType;
import com.gameart.async.api.IAsyncListener;

/***异步任务对象
 *@author JackLei
 *@Date 下午 4:22 2018/9/3
 ***/
public class AsyncObject {
    private AsyncType type;
    private Class mapperClazz;
    private String methodId;
    private ParamBuilder builder;
    private IAsyncListener listener;

    public AsyncObject(AsyncType type, Class mapperClazz, String methodId, ParamBuilder builder) {
        this.type = type;
        this.mapperClazz = mapperClazz;
        this.methodId = methodId;
        this.builder = builder;
    }

    public AsyncObject(AsyncType type, Class mapperClazz, String methodId, ParamBuilder builder, IAsyncListener listener) {
        this.type = type;
        this.mapperClazz = mapperClazz;
        this.methodId = methodId;
        this.builder = builder;
        this.listener = listener;
    }

    public AsyncType getType() {
        return type;
    }

    public Class getMapperClazz() {
        return mapperClazz;
    }

    public String getMethodId() {
        return methodId;
    }

    public ParamBuilder getBuilder() {
        return builder;
    }

    public IAsyncListener getListener() {
        return listener;
    }

    @Override
    public String toString() {
        return "AsyncObject{" +
                "type=" + type +
                ", mapperClazz=" + mapperClazz +
                ", methodId='" + methodId + '\'' +
                ", builder=" + builder +
                ", listener=" + listener +
                '}';
    }
}
