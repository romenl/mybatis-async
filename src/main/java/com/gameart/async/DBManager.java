package com.gameart.async;


import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;

/***
 *@author JackLei
 *@Date 下午 8:34 2018/8/27
 ***/
public class DBManager {
    private static final DBManager INSTANCE = new DBManager();
    private static SqlSessionFactory factory;
    private static final Logger LOGGER = LoggerFactory.getLogger(DBManager.class);

    private DBManager(){
    }

    public static DBManager getInstance(){
        return INSTANCE;
    }

    public void start() throws IOException {
        Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
        factory = new SqlSessionFactoryBuilder().build(reader);
    }

    public Configuration getConfiguration(){
        return factory.getConfiguration();
    }

    public   SqlSessionFactory getFactory() {
        return factory;
    }

    public static void main(String[] args) throws IOException {
        DBManager.getInstance().start();

    }

}
