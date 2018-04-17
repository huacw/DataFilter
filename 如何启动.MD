##启动步骤:

运行启动类 PermissionExampleApplication.java  

分别访问：

<http://localhost:8080/getExamList>   
<http://localhost:8080/getExamList2>

其中  
getExamList 启动了权限过滤

返回：

```
[{"id":1,"username":"高元","regionCd":""}]
```

getExamList2 没有权限过滤

返回：

```
[{"id":1,"username":"高元","regionCd":"320101"},{"id":2,"username":"李豪山","regionCd":"320202"}]
```

