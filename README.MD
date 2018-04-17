# 数据权限管理中心

>由于公司大部分项目都是使用mybatis，也是使用mybatis的拦截器进行分页处理，所以技术上也直接选择从拦截器入手

## 需求场景

#### 第一种场景：行级数据处理

原sql：

```
select id,username,region from sys_user ;
```

需要封装成：

```
select * from (
    select id,username,region from sys_user 
) where 1=1 and region like “3210%";
```

解释

>用户只能查询当前所属市以及下属地市数据
>其中 like 部分也可以为动态参数（下面会讲到）


此场景还有以下情况:

```
# 判断
select * from (select id,username,region from sys_user ) where 1=1 and region != 320101;
# 枚举
select * from (select id,username,region from sys_user ) where 1=1 and region in (320101,320102,320103);
...
```

#### 第二种场景：列级数据处理
原sql：

```
select id,username,region from sys_user ;
```

* 用户A可以看到 id,username,region 
* 用户B只能查看 id,username 的值，region的值没有权限查看。

## 技术实现

#### mybatis拦截器

在编写mybatis的拦截器之前，我们先来了解下mybaits的拦截目标方法

```
1、Executor
  (update, query, flushStatements, commit,  rollback, getTransaction, close, isClosed)

2、ParameterHandler
  (getParameterObject, setParameters)

3、StatementHandler
  (prepare, parameterize, batch, update, query)

4、ResultSetHandler
  (handleResultSets, handleOutputParameters)
```

这里选择StatementHandler 的 **prepare** 方法作为sql执行之前的拦截进行sql封装，使用ResultSetHandler 的 **handleResultSets** 方法作为sql执行之后的结果拦截过滤。

#### sql执行前
`PrepareInterceptor.java`

```
/**
 * mybatis数据权限拦截器 - prepare
 * @author GaoYuan
 * @date 2018/4/17 上午9:52
 */
@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class,Integer.class })
})
@Component
public class PrepareInterceptor implements Interceptor {
    /** 日志 */
    private static final Logger log = LoggerFactory.getLogger(PrepareInterceptor.class);

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {}

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if(log.isInfoEnabled()){
            log.info("进入 PrepareInterceptor 拦截器...");
        }
        if(invocation.getTarget() instanceof RoutingStatementHandler) {
            RoutingStatementHandler handler = (RoutingStatementHandler) invocation.getTarget();
            StatementHandler delegate = (StatementHandler) ReflectUtil.getFieldValue(handler, "delegate");
            //通过反射获取delegate父类BaseStatementHandler的mappedStatement属性
            MappedStatement mappedStatement = (MappedStatement) ReflectUtil.getFieldValue(delegate, "mappedStatement");
            //千万不能用下面注释的这个方法，会造成对象丢失，以致转换失败
            //MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
            PermissionAop permissionAop = PermissionUtils.getPermissionByDelegate(mappedStatement);
            if(permissionAop == null){
                if(log.isInfoEnabled()){
                    log.info("数据权限放行...");
                }
                return invocation.proceed();
            }
            if(log.isInfoEnabled()){
                log.info("数据权限处理【拼接SQL】...");
            }
            BoundSql boundSql = delegate.getBoundSql();
            ReflectUtil.setFieldValue(boundSql, "sql", permissionSql(boundSql.getSql()));
        }
        return invocation.proceed();
    }

    /**
     * 权限sql包装
     * @author GaoYuan
     * @date 2018/4/17 上午9:51
     */
    protected String permissionSql(String sql) {
        StringBuilder sbSql = new StringBuilder(sql);
        String userMethodPath = PermissionConfig.getConfig("permission.client.userid.method");
        //当前登录人
        String userId = (String)ReflectUtil.reflectByPath(userMethodPath);
        //如果用户为 1 则只能查询第一条
        if("1".equals(userId)){
            //sbSql = sbSql.append(" limit 1 ");
            //如果有动态参数 regionCd
            if(true){
                String premission_param = "regionCd";
                //select * from (select id,name,region_cd from sys_exam ) where region_cd like '${}%'
                String methodPath = PermissionConfig.getConfig("permission.client.params." + premission_param);
                String regionCd = (String)ReflectUtil.reflectByPath(methodPath);
                sbSql = new StringBuilder("select * from (").append(sbSql).append(" ) s where s.regionCd like concat("+ regionCd +",'%')  ");
            }

        }
        return sbSql.toString();
    }
}
```

#### sql执行后
`ResultInterceptor.java`

```
/**
 * mybatis数据权限拦截器 - handleResultSets
 * 对结果集进行过滤
 * @author GaoYuan
 * @date 2018/4/17 上午9:52
 */
@Intercepts({
        @Signature(type = ResultSetHandler.class, method = "handleResultSets", args={Statement.class})
})
@Component
public class ResultInterceptor implements Interceptor {
    /** 日志 */
    private static final Logger log = LoggerFactory.getLogger(ResultInterceptor.class);

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {}

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if(log.isInfoEnabled()){
            log.info("进入 ResultInterceptor 拦截器...");
        }
        ResultSetHandler resultSetHandler1 = (ResultSetHandler) invocation.getTarget();
        //通过java反射获得mappedStatement属性值
        //可以获得mybatis里的resultype
        MappedStatement mappedStatement = (MappedStatement)ReflectUtil.getFieldValue(resultSetHandler1, "mappedStatement");
        //获取切面对象
        PermissionAop permissionAop = PermissionUtils.getPermissionByDelegate(mappedStatement);

        //执行请求方法，并将所得结果保存到result中
        Object result = invocation.proceed();
        if(permissionAop != null) {
            if (result instanceof ArrayList) {
                ArrayList resultList = (ArrayList) result;
                for (int i = 0; i < resultList.size(); i++) {
                    Object oi = resultList.get(i);
                    Class c = oi.getClass();
                    Class[] types = {String.class};
                    Method method = c.getMethod("setRegionCd", types);
                    // 调用obj对象的 method 方法
                    method.invoke(oi, "");
                    if(log.isInfoEnabled()){
                        log.info("数据权限处理【过滤结果】...");
                    }
                }
            }
        }
        return result;
    }
}
```

其中 `PermissionAop` 为 dao 层自定义切面，用于开关控制是否启用数据权限过滤。


## 难点

1. 如何在拦截器获取dao层注解内容；
2. 如何获取当前登录人标识；
3. 如何传递动态参数；
4. 需要考虑到与sql分页的优先级。

## 解答

#### 拦截器获取dao层注解

不同方法的拦截器获取方法稍微有所区别，具体在上面的 `PrepareInterceptor.java` 与 `ResultInterceptor.java` 代码中自行查看。

#### 获取当前登录人标识

由于不同框架或者不同项目，获取当天登录人的方法可能不一样，那么就只能通过配置的方式动态将`获取当前登录人的方法`传递给权限中心。
配置文件中添加：

```
# 客户端获取当前登录人标识
permission.client.userid.method=com.raising.sc.permission.example.util.UserUtils.getUserId
```

然后利用**Java反射机制**，触发getUserId( )方法。

#### 传递动态参数

比如用户A只能查询自己单位以及下属单位的所有数据；
配置中心配置的where部分的sql如下：

```
org_cd like concat(${orgCd},'%')
```

然后通过`PrepareInterceptor.java`读取到以上sql，并且通过数据库或者配置文件中设置的参数【orgCd】相关联的方法（类似`获取当前登录人标识`的方式），提前在权限参数（orgCd）配置好对应的方法路径、参数值类型、返回值类型等。

配置文件或者数据库获取到 orgCd 对应的方法路径：

```
com.raising.sc.permission.example.util.UserUtils.getRegionCdByUserId
```

当然，现在这样只是简单的动态参数，其余的还需要后续的开发，这里只是最简单的尝试。


## 拓展
从产品的角度来说，此模块需要有三个部分组成：

> 1、foruo-permission-admin  数据权限管理平台
> 2、foruo-permission-server 数据权限服务端（提供权限相关接口）
> 3、foruo-permission-client 数据权限客户端（封装API）

在结合 `应用链路逻辑图` 即可完成此模块内容。




## 涉及知识点:

* Mybatis拦截器
* Java反射机制


## 项目源码

码云：<https://gitee.com/gmarshal/foruo-sc-permission>

## 博客地址

https://my.oschina.net/gmarshal/blog/1797026


