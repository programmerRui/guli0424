package com.neusoft.mangerservice.margerserviceimpl;

import com.alibaba.dubbo.config.annotation.Service;
import com.neusoft.bean.po.*;
import com.neusoft.interfaces.SPUService;
import com.neusoft.mangerservice.dao.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class SPUServiceImpl implements SPUService {
    @Autowired
    SpuInfoMapper spuInfoMapper;
    @Autowired
    BaseSaleAttrMapper baseSaleAttrMapper;
    @Autowired
    SpuSaleAttrMapper spuSaleAttrMapper;
    @Autowired
    SpuSaleAttrValueMapper spuSaleAttrValueMapper;
    @Autowired
    SpuImageMapper spuImageMapper;

    @Override
    public List<SpuInfo> spuList(String catalog3Id) {
        SpuInfo spuInfo = new SpuInfo();
        spuInfo.setCatalog3Id(Long.parseLong(catalog3Id));
        return spuInfoMapper.select(spuInfo);
    }

    @Override
    public List<BaseSaleAttr> baseSaleAttrList() {
        return baseSaleAttrMapper.selectAll();
    }

    @Override
    public void saveSpu(SpuInfo spuInfo) {
        //保存spuInfo
        spuInfoMapper.insertSelective(spuInfo);
        Long spuId = spuInfoMapper.getMaxId();

        System.out.println(spuId);
        //保存spu销售属性
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
            spuSaleAttr.setSpuId(spuId);
            spuSaleAttrMapper.insertSelective(spuSaleAttr);
            List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
            //保存spu销售属性值
            for (SpuSaleAttrValue spuSaleAttrValue : spuSaleAttrValueList) {
                spuSaleAttrValue.setSpuId(spuId);
                spuSaleAttrValueMapper.insert(spuSaleAttrValue);
            }
        }
        //保存图片信息
        List<SpuImage> imageList = spuInfo.getSpuImageList();
        for (SpuImage spuImage : imageList) {
            spuImage.setSpuId(spuId);
            spuImageMapper.insert(spuImage);
        }
    }

    @Override
    public Long getMaxId() {
        return spuInfoMapper.getMaxId();
    }


}
