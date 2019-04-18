package com.neusoft.interfaces;

import com.neusoft.bean.po.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

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

    List<SpuSaleAttr> selectSpuSaleAttrListCheckBySku(Map<String, Long> stringStringHashMap);

    List<SkuInfo> selectSkuSaleAttrValueListBySpu(@Param("spuId") Long spuId);
}
