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

    public boolean putMethod(String methodNameRef, Method method){
        if(methods.containsKey(methodNameRef)){
            return false;
        }
        methods.put(methodNameRef,method);
        return true;
    }

    public Method getMethod(String methodNameRef){
        return methods.get(methodNameRef);
    }
}
