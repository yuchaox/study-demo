package com.jike.dynamicdatasource.annotation;

/**
 * DataSource
 *
 * @author yuchaochao
 * @version V1.0
 * @date 2019/10/22 8:57
 **/

import java.lang.annotation.*;

/**
 * 备注：自定义数据源选择注解
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataSource {
    String name() default "";
}
