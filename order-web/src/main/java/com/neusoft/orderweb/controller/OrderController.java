package com.neusoft.orderweb.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.neusoft.bean.po.CartInfo;
import com.neusoft.bean.po.OrderDetail;
import com.neusoft.bean.po.OrderInfo;
import com.neusoft.bean.po.UserAddress;
import com.neusoft.interfaces.CartService;
import com.neusoft.interfaces.OrderService;
import com.neusoft.interfaces.UserService;
import com.neusoft.utils.annotation.LoginRequire;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class OrderController {
    @Reference
    OrderService orderService;
    @Reference
    CartService cartService;
    @Reference
    UserService userService;
    @RequestMapping("selectAll.do")
    @ResponseBody
    public List<OrderInfo> selectAll(){
        return orderService.selectOrder();
    }

    @LoginRequire(ifNeedSuccess = true)
    @RequestMapping("toTrade")
    public String toTrade(HttpServletRequest request, HttpServletResponse response, ModelMap map){
        String userId=(String) request.getAttribute("userId");
        //将选中的购物车对象转化为订单对象，展示出来
       List<CartInfo> cartInfos=cartService.getCartCacheByChecked(userId);
       List<OrderDetail> orderDetails=new ArrayList<>();
        for (CartInfo cartInfo : cartInfos) {
            OrderDetail orderDetail = new OrderDetail();
            //将购物车对象转化为订单对象
            orderDetail.setImgUrl(cartInfo.getImgUrl());
            orderDetail.setOrderPrice(cartInfo.getCartPrice());
            orderDetail.setSkuNum(cartInfo.getSkuNum().toString());
            orderDetail.setSkuId(cartInfo.getSkuId());
            orderDetails.add(orderDetail);
        }

        //查询用户收货地址，让用户选择
        List<UserAddress> userAddress = userService.getUserAddressList(userId);

        map.put("userAddressList",userAddress);
        map.put("orderDetailList",orderDetails);
        map.put("totalAmount",gettotalPrice(cartInfos));
        return "Trade";
    }
    private BigDecimal gettotalPrice(List<CartInfo> cartInfos) {
        BigDecimal b=new BigDecimal("0");
        for (CartInfo cartInfo : cartInfos) {
            if(cartInfo.getIsChecked().equals("1")){
                b=b.add(cartInfo.getCartPrice());
            }
        }
        return b;
    }
}
