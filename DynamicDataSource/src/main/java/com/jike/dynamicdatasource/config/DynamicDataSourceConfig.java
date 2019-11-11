package com.jike.dynamicdatasource.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * DynamicDataSource
 *
 * @author yuchaochao
 * @version V1.0
 * @date 2019/10/22 8:59
 **/
@Configuration
@Component
public class DynamicDataSourceConfig {
    @Bean
    @ConfigurationProperties("spring.datasource.druid.yc-master")
    public DataSource ycMasterDataSource(){
        return  DruidDataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.druid.yc-slave")
    public DataSource ycSlaveDataSource(){
        return DruidDataSourceBuilder.create().build();
    }

    @Bean
    @Primary
    public DynamicDataSource dataSource(DataSource ycMasterDataSource, DataSource ycSlaveDataSource) {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("yc-master",ycMasterDataSource);
        targetDataSources.put("yc-slave", ycSlaveDataSource);
        return new DynamicDataSource(ycMasterDataSource, targetDataSources);
    }
}
