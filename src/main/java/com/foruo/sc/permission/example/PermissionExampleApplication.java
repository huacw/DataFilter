package com.foruo.sc.permission.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 启动类
 * @author GaoYuan
 * @date 2018/4/17 下午2:42
 */
@SpringBootApplication
//开启事务管理
@EnableTransactionManagement
public class PermissionExampleApplication {

    public static void main(String[] args){
        SpringApplication.run(PermissionExampleApplication.class,args);
    }

}
