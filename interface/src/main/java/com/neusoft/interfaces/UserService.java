package com.neusoft.interfaces;


import com.neusoft.bean.po.UserInfo;

import java.util.List;

public interface UserService {
    List<UserInfo> selectUser();
}
