package com.neusoft.userservice.serviceimpl;

import com.alibaba.dubbo.config.annotation.Service;
import com.neusoft.bean.po.UserInfo;
import com.neusoft.userservice.dao.UserInfoMapper;
import com.neusoft.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserInfoMapper infoMapper;
    @Override
    public List<UserInfo> selectUser() {
        List<UserInfo> userInfos = infoMapper.selectAll();
        return userInfos;
    }
}
