package com.neusoft.orderservice.serviceimpl;

import com.alibaba.dubbo.config.annotation.Service;
import com.neusoft.bean.po.OrderInfo;
import com.neusoft.interfaces.OrderService;
import com.neusoft.orderservice.dao.OrderInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    @Autowired
    OrderInfoMapper orderInfoMapper;
    @Override
    public List<OrderInfo> selectOrder() {
        return orderInfoMapper.selectAll();
    }
}
