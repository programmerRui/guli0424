package com.neusoft.interfaces;


import com.neusoft.bean.po.UserAddress;
import com.neusoft.bean.po.UserInfo;

import java.util.List;

public interface UserService {
    List<UserInfo> selectUser();

    UserInfo login(UserInfo userInfo);

    List<UserInfo> userInfoList();

    List<UserAddress> getUserAddressList(String userId);

    UserAddress getUserAddressByAddressId(String deliveryAddress);
}
