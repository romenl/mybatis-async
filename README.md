# mybatis-async
mybatis-async,是封装了mybatis的异步框架。

##逻辑图如下：
 ![image](https://github.com/LeiXuan6/mybatis-async/raw/master/src/test/java/com/gameart/async/img/mybatis-async.jpg)

## 工程依赖
+ Jdk 1.8
+ Maven 3.x
+ mybits 3.x

## 调用示例
+ 实现规范

   + AsyncMapper 注解, 让Mapper具有异步的能力
   + AsyncMethod 注解, 标记Mapper的某个方法是异步调用的
   + AsyncType   注解, 异步的类型

+ Mapper实现规范的示列

```
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


``` 

+ 异步任务提交示列
```
     AsyncService.commitAsyncTask(AsyncType.SELECT, SubjectMapper.class, "query", ParamBuilder.create().addInteger(1), new IAsyncListener() {
                            @Override
                            public void callBack(Object o) {
                                System.out.println(o);
                            }
                        });
```
 + 具体列子参考TestInsert,TestQuery,TestUpdate
