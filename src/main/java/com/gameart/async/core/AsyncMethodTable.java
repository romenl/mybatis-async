package com.gameart.async.core;


import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/***
 *@author JackLei
 *@Date 下午 3:27 2018/8/31 0031
 ***/
public class AsyncMethodTable {

    private Map<String,Method> methods =   new HashMap<>();

    public void putMethod(String methodNameRef, Method method){

        methods.put(methodNameRef,method);
    }

    public Method getMethod(String methodNameRef){
        return methods.get(methodNameRef);
    }
}
