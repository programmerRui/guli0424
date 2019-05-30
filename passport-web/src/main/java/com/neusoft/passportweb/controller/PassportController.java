package com.neusoft.passportweb.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.neusoft.bean.po.CartInfo;
import com.neusoft.bean.po.UserInfo;
import com.neusoft.interfaces.CartService;
import com.neusoft.interfaces.UserService;
import com.neusoft.utils.util.CookieUtil;
import com.neusoft.utils.util.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class PassportController {
    @Reference
    UserService userService;
    @Reference
    CartService cartService;
    @RequestMapping("index")
    public String index(String returnURL, ModelMap map){
        map.put("returnURL",returnURL);
        return "index";
    }
    @RequestMapping("login")
    @ResponseBody
    public String login(HttpServletRequest request, UserInfo userInfo){
        //调用用户服务验证用户名和密码
        UserInfo user=userService.login(userInfo);
        //颁发tooken
        if(user==null){
            return "username or password err";
        }else{
            HashMap<String, String> stringStringHashMap = new HashMap<>();
            stringStringHashMap.put("userId",user.getId().toString());
            stringStringHashMap.put("nickName",user.getNickName());
            String ip="";
            String token = JwtUtil.encode("guli", stringStringHashMap, getMyId(request));
            //同步购物车数据
            String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
            List<CartInfo> cartInfos=null;
            if(StringUtils.isNotBlank(cartListCookie)){
                cartInfos= JSON.parseArray(cartListCookie,CartInfo.class);
            }
            cartService.combinCart(cartInfos,user.getId());
            return token;
        }
        //重定项原始业务

    }

    private String getMyId(HttpServletRequest request) {
        String ip="";
        ip= request.getHeader("x-forwarded-for");
        if(StringUtils.isBlank(ip)){
            ip= request.getRemoteAddr();//直接获取ip
        }
        if(StringUtils.isBlank(ip)){
            ip= "127.0.0.1";//设置虚拟ip
        }
        return ip;
    }

    @RequestMapping("verify")
    @ResponseBody
    public String verify(String token,String salt){
        Map<String,String> userMap=null;
        try {
            userMap = JwtUtil.decode("guli", token, salt);
        }catch (Exception e){
            return "fail";
        }

        if(userMap!=null){
            return "success";
        }else{
            return "fail";
        }

    }
}
