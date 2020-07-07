package com.pony.test.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.pony.test.dao.AdminDao;
import com.pony.test.pojo.Admin;
import com.pony.test.pojo.Student;
import com.pony.test.service.AdminService;
import com.pony.test.utils.Assert;
import com.pony.test.wx.WxUser;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    AdminDao dao;

    @Autowired
    StudentServiceImpl studentService;

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
        return StringUtils.isNotBlank(openId) ? dao.getByOpenId(openId) : null;
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

    /**
     * 注册新账号
     *
     * @param [phone, stuNumber, password]
     * @return 2020/6/14 14:45
     * @Author huangzhanping
     */
    @Override
    public void add(String phone, String stuNumber, String password) {
        Student student = studentService.get(stuNumber);
        Assert.state(student != null, "该学号不存在");
        Admin admin = dao.getByStudent(student.getId());
        Assert.state(admin != null, "该学号已被注册");
    }


    public String count(String stuNumber) {
        Student student = studentService.get(stuNumber);
        Admin admin = dao.getByStudent(student.getId());
        return JSONObject.toJSONString(admin);
    }

}
