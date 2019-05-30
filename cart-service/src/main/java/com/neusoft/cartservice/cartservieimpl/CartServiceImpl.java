package com.neusoft.cartservice.cartservieimpl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.neusoft.bean.po.CartInfo;
import com.neusoft.cartservice.dao.CartInfoMapper;
import com.neusoft.interfaces.CartService;
import com.neusoft.serviceutil.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class CartServiceImpl implements CartService {
    @Autowired
    CartInfoMapper cartInfoMapper;
    @Autowired
    RedisUtil redisUtil;

    @Override
    public CartInfo ifCartExists(CartInfo cartInfo) {
        CartInfo cartInfo1 = new CartInfo();
        cartInfo1.setUserId(cartInfo.getUserId());
        cartInfo1.setSkuId(cartInfo.getSkuId());
        CartInfo selectOne = cartInfoMapper.selectOne(cartInfo1);
        return selectOne;
    }

    @Override
    public void updateCart(CartInfo cartInfoDb) {
        cartInfoMapper.updateByPrimaryKeySelective(cartInfoDb);
    }

    @Override
    public void saveCart(CartInfo cartInfo) {
        cartInfoMapper.insertSelective(cartInfo);
    }

    @Override
    public void syncCache(String userId) {
        Jedis jedis = redisUtil.getJedis();
        CartInfo cartInfo = new CartInfo();
        cartInfo.setUserId(Long.parseLong(userId));
        List<CartInfo> select = cartInfoMapper.select(cartInfo);
        Map<String, String> stringStringHashMap = new HashMap<>();
        for (CartInfo info : select) {
            stringStringHashMap.put(info.getId().toString(), JSON.toJSONString(info));
        }
        jedis.hmset("carts:"+userId+":info",stringStringHashMap);
    }

    @Override
    public List<CartInfo> getCartCache(String userId) {
        List<CartInfo> cartInfos=new ArrayList<>();
        Jedis jedis = redisUtil.getJedis();
        List<String> hvals = jedis.hvals("carts:" + userId + ":info");
        if(hvals!=null&&hvals.size()>0){
            for (String hval : hvals) {
                CartInfo cartInfo = JSON.parseObject(hval, CartInfo.class);
                cartInfos.add(cartInfo);
            }
        }
        return cartInfos;
    }

    @Override
    public void updateCartChecked(CartInfo cartInfo) {
        //update table set a=1,b=2 where id=? and id2=?
        Example e=new Example(CartInfo.class);
        e.createCriteria().andEqualTo("skuId",cartInfo.getSkuId()).andEqualTo("userId",cartInfo.getUserId());
        //cartInfo为set的内容,e为where 后面的条件
        cartInfoMapper.updateByExampleSelective(cartInfo,e);
        syncCache(cartInfo.getUserId().toString());
    }

    @Override
    public void combinCart(List<CartInfo> cartInfos, Long id) {
        if(cartInfos!=null){
            for (CartInfo cartInfo : cartInfos) {
                CartInfo info = ifCartExists(cartInfo);
                if(info==null){
                    //插入数据
                    cartInfo.setUserId(id);
                    cartInfoMapper.insertSelective(cartInfo);
                }else{
                    //更新
                    info.setSkuNum(cartInfo.getSkuNum()+info.getSkuNum());
                    info.setCartPrice(info.getSkuPrice().multiply(new BigDecimal(info.getSkuNum())));
                    cartInfoMapper.updateByPrimaryKeySelective(info);
                }
            }
        }
        //同步缓存
        syncCache(id.toString());
    }

    @Override
    public List<CartInfo> getCartCacheByChecked(String userId) {
        List<CartInfo> cartInfos=new ArrayList<>();
        Jedis jedis = redisUtil.getJedis();
        List<String> hvals = jedis.hvals("carts:" + userId + ":info");
        if(hvals!=null&&hvals.size()>0){
            for (String hval : hvals) {
                CartInfo cartInfo = JSON.parseObject(hval, CartInfo.class);
                if(cartInfo.getIsChecked().equals("1")){
                    cartInfos.add(cartInfo);
                }
            }
        }
        return cartInfos;
    }
}
