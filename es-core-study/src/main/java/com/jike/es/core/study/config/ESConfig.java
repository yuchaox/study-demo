package com.jike.es.core.study.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Objects;

/**
 * ESConfig
 * es RestHighLevelClient 配置
 * es 7.x之后 只支持http
 * @author yuchaochao
 * @version V1.0
 * @date 2019/11/8 11:04
 **/
@Configuration
@Slf4j
public class ESConfig {
    private static final int ADDRESS_LENGTH = 2;
    /**
     *请求协议
     */
    private static final String HTTP_SCHEME = "http";
    /**
     *权限验证
     */
    final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    /**
     * 使用冒号隔开ip和端口
     */
    @Value("${elasticsearch.address}")
    private String[] address;

//    @Value("${elasticsearch.username}")
//    private String username;

//    @Value("${elasticsearch.password}")
//    private String password;


    @Bean
    public RestClientBuilder restClientBuilder() {
        HttpHost[] hosts = Arrays.stream(address)
                .map(this::makeHttpHost)
                .filter(Objects::nonNull)
                .toArray(HttpHost[]::new);
        log.debug("hosts:{}", Arrays.toString(hosts));
        //配置权限验证
        //credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
        RestClientBuilder restClientBuilder = RestClient.builder(hosts).setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
            @Override
            public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
            }
        });
        return restClientBuilder;
    }


    @Bean(name = "highLevelClient")
    public RestHighLevelClient highLevelClient(@Autowired RestClientBuilder restClientBuilder) {
        //重试时间
        //restClientBuilder.setMaxRetryTimeoutMillis(60000);
        return new RestHighLevelClient(restClientBuilder);
    }

    /**
     * 处理请求地址
     * @param addressStr
     * @return HttpHost
     */
    private HttpHost makeHttpHost(String addressStr){
        assert StringUtils.isNotEmpty(addressStr);
        String[] address= addressStr.split(":");
        if(address.length== ADDRESS_LENGTH){
            String ip = address[0];
            int port = Integer.parseInt(address[1]);
            return  new HttpHost(ip, port,HTTP_SCHEME);
        }else{
            return null;
        }
    }

}
