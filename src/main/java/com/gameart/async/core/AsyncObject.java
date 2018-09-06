package com.gameart.async.core;
import com.gameart.async.api.IAsyncListener;

/***异步任务对象
 *@author JackLei
 *@Date 下午 4:22 2018/9/3
 ***/
public class AsyncObject {
    private Class mapperClazz;
    private String methodId;
    private ParamBuilder builder;
    private IAsyncListener listener;

    public AsyncObject(Class mapperClazz, String methodId, ParamBuilder builder, IAsyncListener listener) {
        this.mapperClazz = mapperClazz;
        this.methodId = methodId;
        this.builder = builder;
        this.listener = listener;
    }

    public AsyncObject(Class mapperClazz, String methodId, ParamBuilder builder) {
        this.mapperClazz = mapperClazz;
        this.methodId = methodId;
        this.builder = builder;
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
                "mapperClazz=" + mapperClazz +
                ", methodId='" + methodId + '\'' +
                ", builder=" + builder +
                ", listener=" + listener +
                '}';
    }
}
