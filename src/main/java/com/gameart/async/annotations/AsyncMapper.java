package com.gameart.async.annotations;

import java.lang.annotation.*;

/***让一个接口具有异步的功能
 *@author JackLei
 *@Date 下午 5:26 2018/8/30
 ***/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AsyncMapper {

}
