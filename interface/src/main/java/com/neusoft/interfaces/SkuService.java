package com.neusoft.interfaces;

import com.neusoft.bean.po.SkuInfo;

import java.util.List;

public interface SkuService {
    List getSkuListBySpu(Long spuId);

    void saveSku(SkuInfo skuInfo);

    void updateSku(SkuInfo skuInfo);

    SkuInfo selectOne(Long skuId);
}
