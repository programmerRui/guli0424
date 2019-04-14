package com.neusoft.mangerservice.margerserviceimpl;

import com.alibaba.dubbo.config.annotation.Service;
import com.neusoft.bean.po.*;
import com.neusoft.interfaces.SpuService;
import com.neusoft.mangerservice.dao.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class SpuServiceImpl implements SpuService {
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
    public List<SpuSaleAttr> getSaleAttrListBySpuId(Long spuId) {
        SpuSaleAttr spuSaleAttr = new SpuSaleAttr();
        spuSaleAttr.setSpuId(spuId);
        List<SpuSaleAttr> spuSaleAttrList = spuSaleAttrMapper.select(spuSaleAttr);
        for (SpuSaleAttr saleAttr : spuSaleAttrList) {
            Long saleAttrId = saleAttr.getSaleAttrId();
            SpuSaleAttrValue spuSaleAttrValue = new SpuSaleAttrValue();
            spuSaleAttrValue.setSpuId(spuId);
            spuSaleAttrValue.setSaleAttrId(saleAttrId);
            List<SpuSaleAttrValue> spuSaleAttrValues = spuSaleAttrValueMapper.select(spuSaleAttrValue);
            saleAttr.setSpuSaleAttrValueList(spuSaleAttrValues);
        }
        return spuSaleAttrList;
    }

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
    //修改
    @Override
    public void updataSpu(SpuInfo spuInfo) {
        Long spuInfoId = spuInfo.getId();
        spuInfoMapper.updateByPrimaryKey(spuInfo);
        SpuImage spuImage = new SpuImage();
        spuImage.setSpuId(spuInfoId);
        //先删除图片信息
        spuImageMapper.delete(spuImage);
        //删除销售属性
        SpuSaleAttrValue spuSaleAttrValue = new SpuSaleAttrValue();
        spuSaleAttrValue.setSpuId(spuInfoId);
        //删除销售属性值
        spuSaleAttrValueMapper.delete(spuSaleAttrValue);
        SpuSaleAttr spuSaleAttr = new SpuSaleAttr();
        spuSaleAttr.setSpuId(spuInfoId);
        //删除销售属性
        spuSaleAttrMapper.delete(spuSaleAttr);
        //重新插入图片信息
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        for (SpuImage image : spuImageList) {
            image.setSpuId(spuInfoId);
            spuImageMapper.insertSelective(image);
        }
        //重新插入销售属性
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        for (SpuSaleAttr saleAttr : spuSaleAttrList) {
            saleAttr.setSpuId(spuInfoId);
            spuSaleAttrMapper.insertSelective(saleAttr);
            //重新插入销售属性值
            List<SpuSaleAttrValue> spuSaleAttrValueList = saleAttr.getSpuSaleAttrValueList();
            for (SpuSaleAttrValue saleAttrValue : spuSaleAttrValueList) {
                saleAttrValue.setSpuId(spuInfoId);
                spuSaleAttrValueMapper.insertSelective(saleAttrValue);
            }
        }
    }

    @Override
    public Long getMaxId() {
        return spuInfoMapper.getMaxId();
    }

    //删除
    @Override
    public int deleteSpu(String spuId) {
        int num = 0;
        SpuImage spuImage = new SpuImage();
        spuImage.setSpuId(Long.parseLong(spuId));
        spuImageMapper.delete(spuImage);
        //删除销售属性值
        SpuSaleAttrValue spuSaleAttrValue = new SpuSaleAttrValue();
        spuSaleAttrValue.setSpuId(Long.parseLong(spuId));
        spuSaleAttrValueMapper.delete(spuSaleAttrValue);
        //删除销售属性
        SpuSaleAttr spuSaleAttr = new SpuSaleAttr();
        spuSaleAttr.setSpuId(Long.parseLong(spuId));
        spuSaleAttrMapper.delete(spuSaleAttr);
        //删除图片信息
        SpuInfo spuInfo = new SpuInfo();
        spuInfo.setId(Long.parseLong(spuId));
        num = spuInfoMapper.delete(spuInfo);
        return num;
    }

    //查询图片
    @Override
    public List<SpuImage> selectImage(Long spuId) {
        SpuImage spuImage = new SpuImage();
        spuImage.setSpuId(spuId);
        return spuImageMapper.select(spuImage);
    }

    //查询销售属性
    @Override
    public List<SpuSaleAttr> getSpuSaleAttr(Long spuId) {
        SpuSaleAttr spuSaleAttr = new SpuSaleAttr();
        spuSaleAttr.setSpuId(spuId);
        List<SpuSaleAttr> spuSaleAttrList = spuSaleAttrMapper.select(spuSaleAttr);
        for (SpuSaleAttr saleAttr : spuSaleAttrList) {
            SpuSaleAttrValue spuSaleAttrValue = new SpuSaleAttrValue();
            spuSaleAttrValue.setSpuId(spuId);
            List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttrValueMapper.select(spuSaleAttrValue);
            saleAttr.setSpuSaleAttrValueList(spuSaleAttrValueList);
        }
        return spuSaleAttrList;
    }
    //获取图片信息
    @Override
    public List<SpuImage> getSpuImageListBySpuId(Long spuId) {
        SpuImage spuImage = new SpuImage();
        spuImage.setSpuId(spuId);
        return spuImageMapper.select(spuImage);
    }



}
