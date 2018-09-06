package com.gameart.async.mybaits;

import com.gameart.async.DBManager;
import com.gameart.async.mybaits.domain.InfoDO;
import com.gameart.async.mybaits.domain.InfoMapper;
import org.apache.ibatis.session.SqlSession;

import java.io.IOException;

/***
 *@author JackLei
 *@Date 上午 11:45 2018/9/6 0006
 ***/
public class Test {
    public static void main(String[] args) throws IOException {
        DBManager.getInstance().start();
        SqlSession sqlSession = DBManager.getInstance().getFactory().openSession();
        InfoMapper mapper = sqlSession.getMapper(InfoMapper.class);
        InfoDO query = mapper.query(10);
        System.out.println(query);
    }
}
