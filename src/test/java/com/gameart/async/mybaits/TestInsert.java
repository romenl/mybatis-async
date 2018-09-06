package com.gameart.async.mybaits;

import com.gameart.async.AsyncConfig;
import com.gameart.async.AsyncService;
import com.gameart.async.api.IAsyncQueueFullListener;
import com.gameart.async.core.ParamBuilder;
import com.gameart.async.exception.IllegalMethodException;
import com.gameart.async.mybaits.domain.InfoDO;
import com.gameart.async.mybaits.domain.InfoMapper;
import com.gameart.async.exception.ConfictMethodException;
import com.gameart.async.exception.IllegalClassException;

import java.io.IOException;

/***
 *@author JackLei
 *@Date 上午 9:32 2018/9/4
 ***/
public class TestInsert {

    public static void main(String[] args) throws IllegalMethodException, IllegalClassException, ConfictMethodException, IOException, InterruptedException {
        AsyncService.start(new AsyncConfig(10_000_000, 3000, 4000,null));
        Thread.sleep(2*1000L);
        int i = 0;
        while(true){

            InfoDO infoDO = new InfoDO();
            infoDO.setTitle("title"+i);
            infoDO.setContent("content"+i);
            i++;
            AsyncService.commitAsyncTask(InfoMapper.class,"insert",ParamBuilder.create().addObject(infoDO));
            if(i/1000!=0){
                i=0;
                Thread.sleep(30L);
            }
        }
    }
}
