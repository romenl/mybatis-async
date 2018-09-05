package com.gameart.async.domain;

import com.gameart.async.annotations.AsyncMapper;
import com.gameart.async.annotations.AsyncMethod;
import com.gameart.async.annotations.AsyncType;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/***
 *@author JackLei
 *@Date 下午 8:57 2018/8/27
 ***/
@AsyncMapper
public interface InfoMapper {

    @AsyncMethod(type = AsyncType.SELECT , id="listAll")
    List<InfoDO> listAll();

    @AsyncMethod(type = AsyncType.INSERT,id="insert")
    int insert(InfoDO infoDO);

    @AsyncMethod(type = AsyncType.UPDATE,id="update")
    int update(InfoDO infoDO);

    @AsyncMethod(type = AsyncType.SELECT,id="query")
    InfoDO query(@Param("id") Integer id);

}
