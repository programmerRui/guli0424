package com.neusoft.itemweb.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.neusoft.bean.po.*;
import com.neusoft.interfaces.SkuService;
import com.neusoft.interfaces.SpuService;
import org.apache.ibatis.annotations.Param;
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
        List<SkuInfo> infos=spuService.selectSkuSaleAttrValueListBySpu(spuId);
        HashMap<String, Long> hashMap = new HashMap<>();
        for (SkuInfo info : infos) {
            Long v = info.getId();
            List<SkuSaleAttrValue> skuSaleAttrValueList = info.getSkuSaleAttrValueList();
            String k="";
            for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
                k+="|"+skuSaleAttrValue.getSaleAttrValueId();
            }
            hashMap.put(k,v);
        }
        String jsonString = JSON.toJSONString(hashMap);
        map.put("skuJson",jsonString);
        //销售属性列表
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
