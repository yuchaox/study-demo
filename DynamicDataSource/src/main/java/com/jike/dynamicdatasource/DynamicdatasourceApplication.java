package com.jike.dynamicdatasource;

import com.jike.dynamicdatasource.config.DynamicDataSourceConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Import;

@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
@MapperScan(basePackages = "com.jike.dynamicdatasource.mapper")
@Import({DynamicDataSourceConfig.class})
public class DynamicdatasourceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DynamicdatasourceApplication.class, args);
    }

}
