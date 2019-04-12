package com.neusoft.mangerweb.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.neusoft.bean.po.BaseSaleAttr;
import com.neusoft.bean.po.SpuInfo;
import com.neusoft.interfaces.SPUService;
import com.neusoft.mangerweb.util.MyUploadUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class SPUController {
    @Reference
    SPUService spuService;

    @RequestMapping("spuListPage.do")
    public String spuListPage(){
        return "spuListPage";
    }

    @RequestMapping("spuList.do")
    @ResponseBody
    public List<SpuInfo> spuList(String catalog3Id){
        return spuService.spuList(catalog3Id);
    }
    @RequestMapping("baseSaleAttrList")
    @ResponseBody
    public List<BaseSaleAttr> baseSaleAttrList(){
        return spuService.baseSaleAttrList();
    }

//    @RequestMapping("maxId")
//    @ResponseBody
//    public Long maxId(){
//        return spuService.maxId();
//    }

    @RequestMapping("saveSpu")
    @ResponseBody
    public String saveSpu(SpuInfo spuInfo){
        spuService.saveSpu(spuInfo);
        return "操作成功";
    }
    @RequestMapping("fileUpload")
    @ResponseBody
    public String fileUpload(@RequestParam("file") MultipartFile file){
        String uploadImage = MyUploadUtil.uploadImage(file);
        return uploadImage;
       // return "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1554867650251&di=b0a2869a95bf6d6d0ca0083690a07b67&imgtype=0&src=http%3A%2F%2Fg-search1.alicdn.com%2Fbao%2Fuploaded%2Fi4%2F1087231708%2FTB2mCLKaqm5V1Bjy1zbXXXsBFXa_%2521%25211087231708.png";
    }
}
