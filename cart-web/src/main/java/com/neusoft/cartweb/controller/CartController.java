package com.neusoft.cartweb.controller;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.neusoft.bean.po.CartInfo;
import com.neusoft.bean.po.SkuInfo;
import com.neusoft.cartweb.util.CookieUtil;
import com.neusoft.interfaces.CartService;
import com.neusoft.interfaces.SkuService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CartController {
    @Reference
    SkuService skuService;
    @Reference
    CartService cartService;
    @RequestMapping("cartSuccess")
    public String cartsuccess(){
        return "success";
    }
    @RequestMapping("addToCart")
    public String addToCart(HttpServletRequest request, HttpServletResponse response, CartInfo cartInfo){
        Long id = cartInfo.getSkuId();
        SkuInfo sku = skuService.selectOne(id);

        cartInfo.setCartPrice(sku.getPrice().multiply(new BigDecimal(cartInfo.getSkuNum())));
        cartInfo.setIsChecked("1");
        cartInfo.setImgUrl(sku.getSkuDefaultImg());
        cartInfo.setCartPrice(sku.getPrice());
        cartInfo.setSkuName(sku.getSkuName());

        String userId = "";
        List<CartInfo> cartInfos = new ArrayList<>();
        if(StringUtils.isBlank(userId)){
            // 用户未登陆，添加cookie
            String cartListCookieStr = CookieUtil.getCookieValue(request, "cartListCookie", true);
            if(StringUtils.isBlank(cartListCookieStr)){
                cartInfos.add(cartInfo);
            }else{
                cartInfos = new ArrayList<>();
                cartInfos = JSON.parseArray(cartListCookieStr, CartInfo.class);
                // 判断是否重复的sku
                boolean b = ifNewCart(cartInfos,cartInfo);
                if(b){
                    cartInfos.add(cartInfo);//添加

                }else{
                    for (CartInfo info : cartInfos) {
                        Long skuId = info.getSkuId();
                        if(skuId.equals(cartInfo.getSkuId())){
                            info.setSkuNum(info.getSkuNum()+cartInfo.getSkuNum());
                            info.setCartPrice(info.getCartPrice().multiply(new BigDecimal(info.getSkuNum())));
                        }
                    }
                }
            }

            // 操作完成后覆盖cookie
            CookieUtil.setCookie(request,response,"cartListCookie", JSON.toJSONString(cartInfos),60*60*24*7,true);

        }else{
            // 用户已登陆，添加db
//            String skuId = cartInfo.getSkuId();
//            // select * from catr_info where sku_id = skuId and user_id = userId
//            cartInfo.setUserId(userId);
//            CartInfo cartInfoDb = cartService.ifCartExists(cartInfo);
//
//            if(cartInfoDb!=null){
//                // 更新数据库
//                cartInfoDb.setSkuNum(cartInfoDb.getSkuNum()+cartInfo.getSkuNum());
//                cartInfoDb.setCartPrice(cartInfoDb.getSkuPrice().multiply(new BigDecimal(cartInfoDb.getSkuNum())));
//                cartService.updateCart(cartInfoDb);
//            }else{
//                // 插入数据库
//                cartService.saveCart(cartInfo);
//            }
//
//            // 同步缓存
//            cartService.syncCache(userId);// skuInfo:skuId:info cart:userId:info (hash)

        }

        return "redirect:/cartSuccess";
    }

    private boolean ifNewCart(List<CartInfo> cartInfos, CartInfo cartInfo) {
        boolean b = true;
        for (CartInfo info : cartInfos) {
            Long skuId = info.getSkuId();
            if(skuId.equals(cartInfo.getSkuId())){
                b = false;
            }
        }
        return b;
    }


}
