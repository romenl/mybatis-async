package com.gameart.async.core;
import java.util.ArrayList;
import java.util.List;

/***
 *@author JackLei
 *@Date 上午 11:10 2018/9/3 0003
 ***/
public class ParamBuilder {
    private List<Object> paramList  = new ArrayList<>();

    private ParamBuilder(){}

    public static ParamBuilder create(){
        return new ParamBuilder();
    }

    public Object[] build(){
        return paramList.toArray();
    }

    public ParamBuilder addInteger(Integer intVal){
        paramList.add(intVal);
        return this;
    }

    public ParamBuilder addString(String strVal){
        paramList.add(strVal);
        return this;
    }

    public ParamBuilder addLong(Long longVal){
        paramList.add(longVal);
        return this;
    }

    public ParamBuilder addFloat(Float floatVal){
        paramList.add(floatVal);
        return this;
    }

    public ParamBuilder addDouble(Double doubleVal){
        paramList.add(doubleVal);
        return this;
    }

    public ParamBuilder addShort(Short shortVal){
        paramList.add(shortVal);
        return this;
    }

    public ParamBuilder addByte(Byte byteVal){
        paramList.add(byteVal);
        return this;
    }

    public ParamBuilder addBoolean(Boolean booleanVal){
        paramList.add(booleanVal);
        return this;
    }

    public ParamBuilder addObject(Object obj){
        paramList.add(obj);
        return this;
    }

    @Override
    public String toString() {
        return "ParamBuilder{" +
                "paramList=" + paramList +
                '}';
    }
}
