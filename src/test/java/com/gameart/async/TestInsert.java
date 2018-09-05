package com.gameart.async;

import com.gameart.async.AsyncService;
import com.gameart.async.annotations.AsyncType;
import com.gameart.async.core.ParamBuilder;
import com.gameart.async.domain.InfoDO;
import com.gameart.async.domain.InfoMapper;
import com.gameart.async.exception.ConfictMethodException;
import com.gameart.async.exception.IllegalClassException;
import com.gameart.async.exception.IllegalMethodException;

import java.io.IOException;

/***
 *@author JackLei
 *@Date 上午 9:32 2018/9/4 0004
 ***/
public class TestInsert {

    public static void main(String[] args) throws IllegalMethodException, IllegalClassException, ConfictMethodException, IOException, InterruptedException {
        AsyncService.start();
        Thread.sleep(2*1000L);
        int i = 0;
        while(true){

            InfoDO infoDO = new InfoDO();
            infoDO.setTitle("title"+i);
            infoDO.setContent("content"+i);
            i++;
            AsyncService.commitAsyncTask(AsyncType.INSERT,InfoMapper.class,"insert",ParamBuilder.create().addObject(infoDO));
            if(i/1000!=0){
                i=0;
                Thread.sleep(30L);
            }
        }
    }
}
