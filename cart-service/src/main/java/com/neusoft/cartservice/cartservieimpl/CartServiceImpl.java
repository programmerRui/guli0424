package com.neusoft.cartservice.cartservieimpl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.neusoft.cartservice.dao.CartInfoMapper;
import com.neusoft.interfaces.CartService;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    CartInfoMapper cartInfoMapper;
}
