package com.gameart.async.api;

/***
 *@author JackLei
 *@Date 下午 4:21 2018/9/6 0006
 ***/
public interface IAsyncQueueFullListener<F> {
    void onFull(F f);
}
