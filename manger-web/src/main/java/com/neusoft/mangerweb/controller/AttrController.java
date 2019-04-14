package com.neusoft.mangerweb.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.neusoft.bean.po.*;
import com.neusoft.interfaces.AttrService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class AttrController {
    @Reference
    AttrService mangerService;

    @RequestMapping("index.do")
    public String index(){
        return "index";
    }
    @RequestMapping("attrListPage.do")
    public String attrListPage(){
        return "attrListPage";
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
    @RequestMapping("saveAttr")
    @ResponseBody
    public String saveAttr(BaseAttrInfo attrInfo){
        mangerService.saveAttr(attrInfo);
        return "操作成功";
    }
    @RequestMapping("deleteAttr.do")
    @ResponseBody
    public int deleteAttr(String attrId){
        int num = mangerService.deleteAttr(attrId);
        return num;
    }
    @RequestMapping("selectAttrValue.do")
    @ResponseBody
    public String selectAttrValue(String attrId){
        List<BaseAttrValue> baseAttrValues = mangerService.selectAttrValue(attrId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("value",baseAttrValues);
        String json=jsonObject.toJSONString();
        return json;
    }
    @RequestMapping("getAttrListByCtg3Id")
    @ResponseBody
    public List<BaseAttrInfo> getAttrListByCtg3Id(Long catalog3Id){
        List<BaseAttrInfo> baseAttrInfoList= mangerService.getAttrListByCtg3Id(catalog3Id);
        return baseAttrInfoList;
    }
}
