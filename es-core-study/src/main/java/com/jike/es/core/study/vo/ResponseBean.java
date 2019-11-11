package com.jike.es.core.study.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * ResponseBean
 *响应实体
 * @author yuchaochao
 * @version V1.0
 * @date 2019/11/8 11:52
 * AllArgsConstructor 会生成一个包含所有变量，同时如果变量使用了NotNull annotation ， 会进行是否为空的校验，
 * 全部参数的构造函数的自动生成，该注解的作用域也是只有在实体类上，参数的顺序与属性定义的顺序一致。
 **/
@Data
@AllArgsConstructor
public class ResponseBean {

    /**
     * 状态码
     */
    private Integer code;
    /**
     * 返回信息
     */
    private String message;
    /**
     * 返回的数据
     */
    private Object data;
}
