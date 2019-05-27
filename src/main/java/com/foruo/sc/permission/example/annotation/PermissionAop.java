package com.foruo.sc.permission.example.annotation;

import java.lang.annotation.*;


/**
 * 数据权限过滤
 * @author GaoYuan
 * @date 2018/4/17 下午2:40
 */
@Target(ElementType.METHOD) 
@Retention(RetentionPolicy.RUNTIME) 
@Documented
public @interface PermissionAop {
    String value() default "";

    //过滤的字段
    String field();

}
