package com.gameart.async;

import com.gameart.async.api.IAsyncQueueFullListener;

/***
 *@author JackLei
 *@Date 下午 4:24 2018/9/6
 ***/
public class AsyncConfig {
    /**异步队列的最大长度*/
    protected int asyncQueueMax;
    /**查询线程每秒执行的查询数*/
    protected int queryTps;
    /**更新线程每秒执行的更新数*/
    protected int updateTps;
    /**队列满了的监听器*/
    protected IAsyncQueueFullListener queueFullListener;

    public AsyncConfig(int asyncQueueMax, int queryTps, int updateTps, IAsyncQueueFullListener queueFullListener) {
        this.asyncQueueMax = asyncQueueMax;
        this.queryTps = queryTps;
        this.updateTps = updateTps;
        this.queueFullListener = queueFullListener;
    }

    @Override
    public String toString() {
        return "AsyncConfig{" +
                "asyncQueueMax=" + asyncQueueMax +
                ", queryTps=" + queryTps +
                ", updateTps=" + updateTps +
                ", queueFullListener=" + queueFullListener +
                '}';
    }
}
