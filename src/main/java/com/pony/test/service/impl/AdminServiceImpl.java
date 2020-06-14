package com.pony.test.service.impl;

import com.pony.test.dao.AdminDao;
import com.pony.test.pojo.Admin;
import com.pony.test.service.AdminService;
import com.pony.test.wx.WxUser;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl  implements AdminService {

    @Autowired
    AdminDao dao;
    @Override
    public Admin get(String name) {
        return dao.get(name);
    }

    @Override
    public Admin getByAccount(String account) {

        return dao.getByAccount(account);
    }

    @Override
    public Admin get(String openId, String unionId) {
        return StringUtils.isNotBlank(openId)?dao.getByOpenId(openId):null;
    }


    @Override
    public void bind(Admin admin, WxUser wxUser) {
        admin.setOpenId(wxUser.getOpenId());
        admin.setPic(wxUser.getHeadImgUrl());
        admin.setName(wxUser.getNickName());
        admin.setSex(wxUser.getWxSex().byteValue());
        admin.setPic(StringUtils.isBlank(wxUser.getHeadImgUrl()) ? "" : wxUser.getHeadImgUrl());
        dao.bind(admin);
    }

}
