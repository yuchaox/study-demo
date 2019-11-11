package com.jike.dynamicdatasource.controller;

import com.jike.dynamicdatasource.entity.TUser;
import com.jike.dynamicdatasource.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * UserController
 *
 * @author yuchaochao
 * @version V1.0
 * @date 2019/10/22 9:15
 **/
@RestController
@RequestMapping
public class UserController {
    @Autowired
    private UserService userMapper;

    @GetMapping("/{name}/list")
    public List<TUser> list(@PathVariable("name")String name){
        if(name.equals("master")){
            return userMapper.queryAllWithMaster();
        }else{
            return userMapper.queryAllWithSlave();
        }
    }
}
