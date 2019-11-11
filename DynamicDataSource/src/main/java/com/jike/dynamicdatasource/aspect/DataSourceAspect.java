package com.jike.dynamicdatasource.aspect;

import com.jike.dynamicdatasource.annotation.DataSource;
import com.jike.dynamicdatasource.config.DynamicDataSource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * DataSource
 *
 * @author yuchaochao
 * @version V1.0
 * @date 2019/10/22 8:57
 **/
@Aspect
@Component
public class DataSourceAspect {


    @Pointcut("@annotation(com.jike.dynamicdatasource.annotation.DataSource)")
    public void dataSourcePointCut() {

    }

    @Around("dataSourcePointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();

        DataSource dataSource = method.getAnnotation(DataSource.class);
        if(dataSource == null){
            DynamicDataSource.setDataSource("yc-master");
        }else {
            DynamicDataSource.setDataSource(dataSource.name());
        }

        try {
            return point.proceed();
        } finally {
            DynamicDataSource.clearDataSource();
        }
    }
}
