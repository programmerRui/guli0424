package com.neusoft.interfaces;

import com.neusoft.bean.po.BaseSaleAttr;
import com.neusoft.bean.po.SpuInfo;

import java.util.List;

public interface SPUService {
    List<SpuInfo> spuList(String catalog3Id);

    List<BaseSaleAttr> baseSaleAttrList();

    void saveSpu(SpuInfo spuInfo);


}
