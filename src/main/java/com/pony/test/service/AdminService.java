package com.pony.test.service;

import com.pony.test.pojo.Admin;
import com.pony.test.wx.WxUser;

public interface AdminService {

    //根据名称获取用户
    Admin get(String name);
    //根据账号获取用户
    Admin getByAccount(String account);

    Admin get(String openId, String unionId);

    void bind(Admin admin, WxUser wxUser);

    void add(String phone, String stuNumber, String password);

}
