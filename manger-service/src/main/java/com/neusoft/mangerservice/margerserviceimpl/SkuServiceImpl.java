package com.neusoft.mangerservice.margerserviceimpl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.neusoft.bean.po.SkuAttrValue;
import com.neusoft.bean.po.SkuImage;
import com.neusoft.bean.po.SkuInfo;
import com.neusoft.bean.po.SkuSaleAttrValue;
import com.neusoft.interfaces.SkuService;
import com.neusoft.mangerservice.dao.SkuAttrValueMapper;
import com.neusoft.mangerservice.dao.SkuImageMapper;
import com.neusoft.mangerservice.dao.SkuInfoMapper;
import com.neusoft.mangerservice.dao.SkuSaleAttrValueMapper;
import com.neusoft.serviceutil.util.RedisUtil;
import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;

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
    @Autowired
    RedisUtil redisUtil;

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
        Jedis jedis = null;
        try {
            jedis = redisUtil.getJedis();
        }catch (Exception e){
            return null;
        }
        SkuInfo skuInfo = null;

        // 查询redis缓存
        String key = "sku:" + skuId + ":info";
        String val = jedis.get(key);

        if("empty".equals(val)){
            System.out.println(Thread.currentThread().getName()+"发现数据库中暂时没有改商品，直接返回空对象");
            return skuInfo;
        }
        if (val==null) {
            System.out.println(Thread.currentThread().getName()+"发现缓存中没有数据，申请分布式锁");
            // 申请缓存锁
            String OK = jedis.set("sku:" + skuId + ":lock", "1", "nx", "px", 5000);

            if("OK".equals(OK)){// 拿到缓存锁

//                try {
//                    Thread.sleep(3000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }

                System.out.println(Thread.currentThread().getName()+"获得分布式锁，开始访问数据");
                // 查询db
                skuInfo = getSkuByIdFormDb(skuId);

                if(skuInfo!=null){
                    System.out.println(Thread.currentThread().getName()+"通过分布式锁，查询到数据，同步缓存");
                    // 同步缓存
                    jedis.set(key, JSON.toJSONString(skuInfo));

                }else{
                    // 通知同伴
                    System.out.println(Thread.currentThread().getName()+"通过分布式锁，没有查询到数据，通知同伴在10秒之内不要访问该sku");
                    jedis.setex("sku:" + skuId + ":info", 10,"empty");
                }

                // 归还缓存锁
                System.out.println(Thread.currentThread().getName()+"归还分布式锁");
                jedis.del("sku:" + skuId + ":lock");

            }else{// 没有拿到缓存锁
                // 自旋
                System.out.println(Thread.currentThread().getName()+"发现分布式锁被占用，开始自旋");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                selectOne(skuId);
            }

        }else{
            // 正常转换缓存数据
            System.out.println(Thread.currentThread().getName()+"正常从缓存中取得数据，返回结果");
            skuInfo = JSON.parseObject(val, SkuInfo.class);
        }
        return skuInfo;
//        Jedis jedis = redisUtil.getJedis();
//        SkuInfo skuInfo=null;
//        //查询redis缓存
//        String key="sku:"+skuId+":info";
//        String val=jedis.get(key);
//        skuInfo=JSON.parseObject(val,SkuInfo.class);
//        if (skuInfo==null){
//            //查询DB
//            skuInfo =getSkuByIdFormDb(skuId);
//            //同步缓存
//            jedis.set(key, JSON.toJSONString(skuInfo));
//        }
//        return skuInfo;
    }

    private SkuInfo getSkuByIdFormDb(Long skuId) {
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
