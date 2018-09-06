package com.gameart.async.mybaits.domain;

import com.gameart.async.annotations.AsyncMapper;
import com.gameart.async.annotations.AsyncMethod;
import com.gameart.async.annotations.AsyncType;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/***
 *@author JackLei
 *@Date 下午 3:09 2018/9/5 0005
 ***/
@AsyncMapper
public interface SubjectMapper {

    @AsyncMethod(type = AsyncType.SELECT,id="listAll")
    List<SubjectDO> listAll();

    @AsyncMethod(type = AsyncType.SELECT,id="query")
    List<SubjectDO> query(@Param("id")int id);

}
