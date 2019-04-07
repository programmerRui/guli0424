package com.neusoft.orderweb.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.neusoft.bean.po.OrderInfo;
import com.neusoft.interfaces.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class OrderController {
    @Reference
    OrderService orderService;
    @RequestMapping("selectAll.do")
    @ResponseBody
    public List<OrderInfo> selectAll(){
        return orderService.selectOrder();
    }
}
