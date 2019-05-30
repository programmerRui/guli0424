package com.neusoft.cartweb.controller;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.neusoft.bean.po.CartInfo;
import com.neusoft.bean.po.SkuInfo;
import com.neusoft.cartweb.util.CookieUtil;
import com.neusoft.interfaces.CartService;
import com.neusoft.interfaces.SkuService;
import com.neusoft.utils.annotation.LoginRequire;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import tk.mybatis.mapper.util.StringUtil;

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


    @LoginRequire
    @RequestMapping("checkCart")
    public String checkCart(HttpServletRequest request,HttpServletResponse response,CartInfo cartInfo, ModelMap map){
        String userId=(String) request.getAttribute("userId");
        List<CartInfo> cartInfos = new ArrayList<>();
        //修改购物车的状态
        if(userId!=null&&userId.length()>0){
            //更新db和缓存
            cartInfo.setUserId(Long.parseLong(userId));
            cartService.updateCartChecked(cartInfo);
        }else {
            //更新cookie
            String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
            if(cartListCookie!=null){
                cartInfos= JSON.parseArray(cartListCookie,CartInfo.class);
                for (CartInfo info : cartInfos) {
                    if(info.getSkuId().equals(cartInfo.getSkuId())){
                        info.setIsChecked(cartInfo.getIsChecked());
                    }
                }
            }
            // 操作完成后覆盖cookie
            CookieUtil.setCookie(request,response,"cartListCookie", JSON.toJSONString(cartInfos),60*60*24*7,true);
        }
        //更新数据后将最新数据查询出来
        if(StringUtils.isBlank(userId)){
            //取cookis中的数据
            String cartListCookieStr = CookieUtil.getCookieValue(request, "cartListCookie", true);
            if(cartListCookieStr!=null){
                cartInfos=JSON.parseArray(cartListCookieStr,CartInfo.class);
            }
        }else{
            //取缓存数据
            cartInfos= cartService.getCartCache(userId);
        }
        map.put("cartList",cartInfos);
        return "cartListInner";
    }

    @LoginRequire
    @RequestMapping("cartList")
    public String cartList(HttpServletRequest request, ModelMap map){
        String userId=(String) request.getAttribute("userId");
        List<CartInfo> cartInfos = new ArrayList<>();
        if(StringUtils.isBlank(userId)){
            //取cookis中的数据
            String cartListCookieStr = CookieUtil.getCookieValue(request, "cartListCookie", true);
            if(cartListCookieStr!=null){
                cartInfos=JSON.parseArray(cartListCookieStr,CartInfo.class);
            }
        }else{
            //取缓存数据
            cartInfos= cartService.getCartCache(userId);
        }
        map.put("cartList",cartInfos);
        map.put("totalPrice",gettotalPrice(cartInfos));
        return "cartList";
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

    @RequestMapping("cartSuccess")
    @LoginRequire
    public String cartsuccess(Long skuId,String skuNum,ModelMap map){
        SkuInfo sku = skuService.selectOne(skuId);
        map.put("skuInfo",sku);
        map.put("skuNum",skuNum);
        return "success";
    }
    @LoginRequire
    @RequestMapping("addToCart")
    public String addToCart(HttpServletRequest request, HttpServletResponse response, CartInfo cartInfo){
        Long id = cartInfo.getSkuId();
        Integer skuNum = cartInfo.getSkuNum();
        SkuInfo sku = skuService.selectOne(id);

        cartInfo.setCartPrice(sku.getPrice().multiply(new BigDecimal(cartInfo.getSkuNum())));
        cartInfo.setIsChecked("1");
        cartInfo.setImgUrl(sku.getSkuDefaultImg());
        cartInfo.setSkuPrice(sku.getPrice());
        cartInfo.setSkuName(sku.getSkuName());

        String userId = (String) request.getAttribute("userId");
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
                            info.setCartPrice(info.getSkuPrice().multiply(new BigDecimal(info.getSkuNum())));
                        }
                    }
                }
            }

            // 操作完成后覆盖cookie
            CookieUtil.setCookie(request,response,"cartListCookie", JSON.toJSONString(cartInfos),60*60*24*7,true);

        }else{
            // 用户已登陆，添加db
            Long skuId = cartInfo.getSkuId();
            // select * from catr_info where sku_id = skuId and user_id = userId
            cartInfo.setUserId(Long.parseLong(userId));
            CartInfo cartInfoDb = cartService.ifCartExists(cartInfo);

            if(cartInfoDb!=null){
                // 更新数据库
                cartInfoDb.setSkuNum(cartInfoDb.getSkuNum()+cartInfo.getSkuNum());
                cartInfoDb.setCartPrice(cartInfoDb.getSkuPrice().multiply(new BigDecimal(cartInfoDb.getSkuNum())));
                cartService.updateCart(cartInfoDb);
            }else{
                // 插入数据库
                cartService.saveCart(cartInfo);
            }
            // 同步缓存
            cartService.syncCache(userId);// skuInfo:skuId:info cart:userId:info (hash)
        }

        return "redirect:cartSuccess?skuId="+id+"&skuNum="+skuNum;
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
