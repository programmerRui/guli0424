package com.neusoft.userservice.serviceimpl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.neusoft.bean.po.UserAddress;
import com.neusoft.bean.po.UserInfo;
import com.neusoft.serviceutil.util.RedisUtil;
import com.neusoft.userservice.dao.UserAddressMapper;
import com.neusoft.userservice.dao.UserInfoMapper;
import com.neusoft.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    UserInfoMapper userInfoMapper;
    @Autowired
    UserAddressMapper userAddressMapper;
    @Autowired
    RedisUtil redisUtil;
    @Override
    public List<UserInfo> selectUser() {
        List<UserInfo> userInfos = userInfoMapper.selectAll();
        return userInfos;
    }

    @Override
    public List<UserInfo> userInfoList() {
        return userInfoMapper.selectAll();
    }

    @Override
    public List<UserAddress> getUserAddressList(String userId) {
        UserAddress userAddress = new UserAddress();
       userAddress.setUserId(Long.parseLong(userId));
        List<UserAddress> select = userAddressMapper.select(userAddress);
        return select;
    }

    @Override
    public UserAddress getUserAddressByAddressId(String deliveryAddress) {
        UserAddress userAddress = new UserAddress();
        userAddress.setId(Long.parseLong(deliveryAddress));
        return userAddressMapper.selectOne(userAddress);
    }

    @Override
    public UserInfo login(UserInfo userInfo) {
        UserInfo selectOne = userInfoMapper.selectOne(userInfo);
        if(selectOne!=null){
            //同步缓存
            Jedis jedis = null;
            try {
                jedis = redisUtil.getJedis();
            }catch (Exception e){
                return null;
            }
            jedis.set("user"+selectOne.getId()+":info", JSON.toJSONString(selectOne));
            jedis.close();
        }
        return selectOne;
    }
}
