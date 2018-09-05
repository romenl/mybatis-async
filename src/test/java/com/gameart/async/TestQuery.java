package com.gameart.async;

import com.gameart.async.annotations.AsyncType;
import com.gameart.async.api.IAsyncListener;
import com.gameart.async.core.ParamBuilder;
import com.gameart.async.domain.InfoMapper;
import com.gameart.async.domain.SubjectMapper;
import com.gameart.async.exception.ConfictMethodException;
import com.gameart.async.exception.IllegalClassException;
import com.gameart.async.exception.IllegalMethodException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/***
 *@author JackLei
 *@Date 上午 9:32 2018/9/4 0004
 ***/
public class TestQuery {
    private static final Logger LOGGER  = LoggerFactory.getLogger(TestQuery.class);
    public static void main(String[] args) throws IllegalMethodException, IllegalClassException, ConfictMethodException, IOException, InterruptedException {
        AsyncService.start();

        new Thread(new Runnable() {

            @Override
            public void run() {
                long lastRecordTime = System.currentTimeMillis();
                int count = 0;
                while(true) {

                    long curent = System.currentTimeMillis();
                    AsyncService.commitAsyncTask(AsyncType.SELECT, InfoMapper.class, "query", ParamBuilder.create().addInteger(10), new IAsyncListener() {
                        @Override
                        public void callBack(Object o) {

                        }
                    });
                    count ++;
                    if(count == 1_000_000){
                        System.out.println(">"+count);
                        break;
                    }
                }
            }
        }).start();


        new Thread(new Runnable() {

            @Override
            public void run() {
                long lastRecordTime = System.currentTimeMillis();
                int count = 0;
                while(true) {

                    long curent = System.currentTimeMillis();
                    AsyncService.commitAsyncTask(AsyncType.SELECT, SubjectMapper.class, "query", ParamBuilder.create().addInteger(1), new IAsyncListener() {
                        @Override
                        public void callBack(Object o) {
                            System.out.println(o);
                        }
                    });
                    count ++;
                    if(count == 1_000_000){
                        System.out.println(">"+count);
                        break;
                    }
                }
            }
        }).start();






    }
}
