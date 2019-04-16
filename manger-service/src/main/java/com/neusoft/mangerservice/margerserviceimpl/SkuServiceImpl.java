package com.neusoft.mangerservice.margerserviceimpl;

import com.alibaba.dubbo.config.annotation.Service;
import com.neusoft.bean.po.SkuAttrValue;
import com.neusoft.bean.po.SkuImage;
import com.neusoft.bean.po.SkuInfo;
import com.neusoft.bean.po.SkuSaleAttrValue;
import com.neusoft.interfaces.SkuService;
import com.neusoft.mangerservice.dao.SkuAttrValueMapper;
import com.neusoft.mangerservice.dao.SkuImageMapper;
import com.neusoft.mangerservice.dao.SkuInfoMapper;
import com.neusoft.mangerservice.dao.SkuSaleAttrValueMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SkuServiceImpl implements SkuService {
    @Autowired
    SkuInfoMapper skuInfoMapper;
    @Autowired
    SkuImageMapper skuImageMapper;
    @Autowired
    SkuAttrValueMapper skuAttrValueMapper;
    @Autowired
    SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Override
    public List<SkuInfo> getSkuListBySpu(Long spuId) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setSpuId(spuId);
        List<SkuInfo> skuInfoList = skuInfoMapper.select(skuInfo);
        return skuInfoList;
    }

    //添加sku
    @Override
    public void saveSku(SkuInfo skuInfo) {
        skuInfoMapper.insertSelective(skuInfo);
        Long maxId = skuInfoMapper.getMaxId();
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        for (SkuAttrValue skuAttrValue : skuAttrValueList) {
            skuAttrValue.setSkuId(maxId);
            skuAttrValueMapper.insert(skuAttrValue);
        }
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
            skuSaleAttrValue.setSkuId(maxId);
            skuSaleAttrValueMapper.insert(skuSaleAttrValue);
        }
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        for (SkuImage skuImage : skuImageList) {
            skuImage.setSkuId(maxId);
            skuImageMapper.insert(skuImage);
        }

    }
    //修改sku
    @Override
    public void updateSku(SkuInfo skuInfo) {
        skuInfoMapper.updateByPrimaryKey(skuInfo);
        Long skuInfoId = skuInfo.getId();
        //删除sku图片信息表
        SkuImage skuImage = new SkuImage();
        skuImage.setSkuId(skuInfoId);
        skuImageMapper.delete(skuImage);
        //删除sku平台属性关联表
        SkuAttrValue skuAttrValue = new SkuAttrValue();
        skuAttrValue.setSkuId(skuInfoId);
        skuAttrValueMapper.delete(skuAttrValue);
        //删除sku销售属性关联表
        SkuSaleAttrValue skuSaleAttrValue = new SkuSaleAttrValue();
        skuSaleAttrValue.setSkuId(skuInfoId);
        skuSaleAttrValueMapper.delete(skuSaleAttrValue);
        //重新插入数据
        //插入sku图片信息
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        for (SkuImage image : skuImageList) {
            image.setSkuId(skuInfoId);
            skuImageMapper.insert(image);
        }
        //插入sku平台属性关联信息
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        for (SkuAttrValue attrValue : skuAttrValueList) {
            attrValue.setSkuId(skuInfoId);
            skuAttrValueMapper.insert(attrValue);
        }
        //插入sku销售属性关联信息
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        for (SkuSaleAttrValue saleAttrValue : skuSaleAttrValueList) {
            saleAttrValue.setSkuId(skuInfoId);
            skuSaleAttrValueMapper.insert(saleAttrValue);
        }
    }

    @Override
    public SkuInfo selectOne(Long skuId) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        SkuInfo selectOne = skuInfoMapper.selectOne(skuInfo);
        SkuImage skuImage = new SkuImage();
        skuImage.setSkuId(skuId);
        List<SkuImage> skuImages = skuImageMapper.select(skuImage);
        selectOne.setSkuImageList(skuImages);
        //spu带的销售属性
        SkuSaleAttrValue skuSaleAttrValue = new SkuSaleAttrValue();
        skuSaleAttrValue.setSkuId(skuId);
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuSaleAttrValueMapper.select(skuSaleAttrValue);
        selectOne.setSkuSaleAttrValueList(skuSaleAttrValueList);
        return selectOne;
    }


}
