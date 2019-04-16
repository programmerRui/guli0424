package com.neusoft.itemweb.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.neusoft.bean.po.SkuInfo;
import com.neusoft.bean.po.SkuSaleAttrValue;
import com.neusoft.bean.po.SpuSaleAttr;
import com.neusoft.bean.po.UserInfo;
import com.neusoft.interfaces.SkuService;
import com.neusoft.interfaces.SpuService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ItemController {
    @Reference
    SkuService skuService;
    @Reference
    SpuService spuService;
    @RequestMapping("{skuId}.html")
    public String show(@PathVariable Long skuId,ModelMap map){
        SkuInfo skuInfo=skuService.selectOne(skuId);
        map.put("skuInfo",skuInfo);
        Long spuId = skuInfo.getSpuId();
        //spu包含的销售属性
        //List<SpuSaleAttr> saleAttrListBySpuId = spuService.getSaleAttrListBySpuId(spuId);
        //当前sku的销售属性
       //List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        Map<String,Long>  stringStringHashMap= new HashMap<>();
        stringStringHashMap.put("spuId",spuId);
        stringStringHashMap.put("skuId",skuId);
        List<SpuSaleAttr> saleAttrListBySpuId = spuService.selectSpuSaleAttrListCheckBySku(stringStringHashMap);
        map.put("spuSaleAttrListCheckBySku",saleAttrListBySpuId);
        return "item";
    }
    @RequestMapping("index.do")
    public String index(ModelMap map){
        map.put("hello","hello thymeleaf");
        List<UserInfo> userInfoList=new ArrayList<UserInfo>();
        for (int i=0;i<3;i++){
            UserInfo userInfo = new UserInfo();
            userInfo.setName("刘德华");
            userInfo.setNickName("肖志睿");
            userInfo.setId(2019L);
            userInfoList.add(userInfo);
        }
        map.put("userInfoLists",userInfoList);
        return "demo";
    }
}
