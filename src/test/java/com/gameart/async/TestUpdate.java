package com.gameart.async;

import com.gameart.async.annotations.AsyncType;
import com.gameart.async.api.IAsyncListener;
import com.gameart.async.core.ParamBuilder;
import com.gameart.async.domain.InfoDO;
import com.gameart.async.domain.InfoMapper;
import com.gameart.async.exception.ConfictMethodException;
import com.gameart.async.exception.IllegalClassException;
import com.gameart.async.exception.IllegalMethodException;

import java.io.IOException;

/***
 *@author JackLei
 *@Date 下午 2:15 2018/9/5 0005
 ***/
public class TestUpdate {
    public static void main(String[] args) throws IllegalMethodException, IllegalClassException, ConfictMethodException, IOException, InterruptedException {
        InfoDO infoDO = new InfoDO();
        infoDO.setId(10);
        infoDO.setTitle("10");
        infoDO.setContent("10");

        new Thread(new Runnable(){

            @Override
            public void run() {

                for(int i =0;;i++) {
                    AsyncService.commitAsyncTask(AsyncType.UPDATE, InfoMapper.class, "update", ParamBuilder.create().addObject(infoDO), new IAsyncListener() {
                        @Override
                        public void callBack(Object o) {

                            System.out.println("update>" + o);

                        }
                    });
                    if(i == 500_000){
                        System.out.println("50W");
                        break;
                    }
                }
            }
        }).start();

        new Thread(new Runnable(){

            @Override
            public void run() {

                for(int i =0; ;i++) {
                    AsyncService.commitAsyncTask(AsyncType.UPDATE, InfoMapper.class, "update", ParamBuilder.create().addObject(infoDO), new IAsyncListener() {
                        @Override
                        public void callBack(Object o) {

                            System.out.println("update>" + o);

                        }
                    });
                    if(i == 500_000){
                        System.out.println("50W" );
                        break;
                    }
                }
            }
        }).start();

        Thread.sleep(10*1000L);
        AsyncService.start();
    }
}
