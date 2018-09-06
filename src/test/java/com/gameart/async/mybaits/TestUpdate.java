package com.gameart.async.mybaits;

import com.gameart.async.AsyncConfig;
import com.gameart.async.AsyncService;
import com.gameart.async.api.IAsyncListener;
import com.gameart.async.api.IAsyncQueueFullListener;
import com.gameart.async.core.ParamBuilder;
import com.gameart.async.mybaits.domain.InfoDO;
import com.gameart.async.mybaits.domain.InfoMapper;
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
        AsyncService.start(new AsyncConfig(10_000_000, 4000, 3000, new IAsyncQueueFullListener() {
            @Override
            public void onFull(Object o) {

            }
        }));

        InfoDO infoDO = new InfoDO();
        infoDO.setId(10);
        infoDO.setTitle("10");
        infoDO.setContent("10");

        new Thread(new Runnable(){

            @Override
            public void run() {

                for(int i =0;;i++) {
                    AsyncService.commitAsyncTask( InfoMapper.class, "update", ParamBuilder.create().addObject(infoDO), new IAsyncListener() {
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
                    AsyncService.commitAsyncTask(InfoMapper.class, "update", ParamBuilder.create().addObject(infoDO), new IAsyncListener() {
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


    }
}
