package com.neusoft.userweb.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.neusoft.bean.po.UserInfo;
import com.neusoft.interfaces.UserService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class UserController {
    @Reference
    UserService userService;
    @RequestMapping("selectAll.do")
    @ResponseBody
    public List<UserInfo> selectAll(){
        return userService.selectUser();
    }
}
