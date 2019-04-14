package com.neusoft.mangerservice.margerserviceimpl;

import com.alibaba.dubbo.config.annotation.Service;
import com.neusoft.bean.po.SkuInfo;
import com.neusoft.interfaces.SkuService;
import com.neusoft.mangerservice.dao.SkuInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class SkuServiceImpl implements SkuService {
    @Autowired
    SkuInfoMapper skuInfoMapper;
    @Override
    public List getSkuListBySpu(Long spuId) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setSpuId(spuId);
        List<SkuInfo> skuInfoList = skuInfoMapper.select(skuInfo);
        return skuInfoList;
    }
}
