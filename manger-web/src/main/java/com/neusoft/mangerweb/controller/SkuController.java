package com.neusoft.mangerweb.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.neusoft.bean.po.SkuInfo;
import com.neusoft.interfaces.SkuService;
import com.neusoft.interfaces.SpuService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class SkuController {
    @Reference
    SkuService skuService;
    @RequestMapping("getSkuListBySpu")
    @ResponseBody
    public List<SkuInfo> getSkuListBySpu(Long spuId){
        List skuListBySpu = skuService.getSkuListBySpu(spuId);
        return skuListBySpu;
    }


}

