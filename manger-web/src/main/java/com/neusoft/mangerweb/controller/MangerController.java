package com.neusoft.mangerweb.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.neusoft.bean.po.BaseAttrInfo;
import com.neusoft.bean.po.BaseCatalog1;
import com.neusoft.bean.po.BaseCatalog2;
import com.neusoft.bean.po.BaseCatalog3;
import com.neusoft.interfaces.MangerService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class MangerController {
    @Reference
    MangerService mangerService;
    @RequestMapping("index.do")
    public String index(){
        return "index";
    }
    @RequestMapping("attrListPage.do")
    public String attrListPage(){
        return "attrListPage";
    }
    @RequestMapping("spuListPage.do")
    public String spuListPage(){
        return "spuListPage";
    }
    @RequestMapping("getCatalog1")
    @ResponseBody
    public List<BaseCatalog1>  getCatalog1(){
        List<BaseCatalog1> catalog1 = mangerService.getCatalog1();
        return catalog1;
    }
    @RequestMapping("getCatalog2")
    @ResponseBody
    public List<BaseCatalog2>  getCatalog2(String catalog1Id){
        List<BaseCatalog2> catalog2 = mangerService.getCatalog2(catalog1Id);
        return catalog2;
    }
    @RequestMapping("getCatalog3")
    @ResponseBody
    public List<BaseCatalog3> getCatalog3(String catalog2Id){
        List<BaseCatalog3> catalog3 = mangerService.getCatalog3(catalog2Id);
        return catalog3;
    }
    @RequestMapping("getAttrList")
    @ResponseBody
    public List<BaseAttrInfo> getAttrList(String catalog3Id){
        List<BaseAttrInfo> attrInfo = mangerService.getAttrInfo(catalog3Id);
        return attrInfo;
    }
}
