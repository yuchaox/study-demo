package com.jike.dynamicdatasource.service;

import com.jike.dynamicdatasource.entity.TUser;
import com.jike.dynamicdatasource.mapper.TUserExample;
import com.jike.dynamicdatasource.mapper.TUserMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * TUserMapper
 *
 * @author yuchaochao
 * @version V1.0
 * @date 2019/10/22 9:00
 **/
@Service
public class UserService {
    @Resource
    private TUserMapper tUserMapper;


    public List<TUser> queryAllWithMaster(){
        TUserExample example= new TUserExample();
        return tUserMapper.selectByExample(example);

    }

    public List<TUser> queryAllWithSlave() {
        TUserExample example= new TUserExample();
        return tUserMapper.selectByExample(example);
    }
}
