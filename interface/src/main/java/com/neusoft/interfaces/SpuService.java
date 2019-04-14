package com.neusoft.interfaces;

import com.neusoft.bean.po.BaseSaleAttr;
import com.neusoft.bean.po.SpuImage;
import com.neusoft.bean.po.SpuInfo;
import com.neusoft.bean.po.SpuSaleAttr;

import java.util.List;

public interface SpuService {
    List<SpuSaleAttr> getSaleAttrListBySpuId(Long spuId);

    List<SpuInfo> spuList(String catalog3Id);

    List<BaseSaleAttr> baseSaleAttrList();

    void saveSpu(SpuInfo spuInfo);

    Long getMaxId();

    int deleteSpu(String spuId);

    List<SpuImage> selectImage(Long spuId);

    List<SpuSaleAttr> getSpuSaleAttr(Long spuId);

    void updataSpu(SpuInfo spuInfo);

    List<SpuImage> getSpuImageListBySpuId(Long spuId);
}
