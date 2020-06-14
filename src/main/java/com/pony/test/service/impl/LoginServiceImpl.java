package com.pony.test.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.pony.test.pojo.Admin;
import com.pony.test.redis.CacheKey;
import com.pony.test.redis.Redis;
import com.pony.test.service.LoginService;
import com.pony.test.utils.Assert;
import com.pony.test.wx.WxCommonService;
import com.pony.test.wx.WxUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {


    private Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);

    @Autowired
    WxCommonService wxCommonService;
    @Autowired
    AdminServiceImpl adminService;



    @Override
    public Admin login(String account, String password, String openId, String iv, String encryptedData) {
        String sessionKey = Redis.getInstance().get(CacheKey.WX_KEY.SP_SERVICE_SESSION_KEY + openId);
        WxUser wxUser = wxCommonService.getUser(sessionKey, iv, encryptedData);
        logger.info("wxUser:{}",JSONObject.toJSONString(wxUser));
        Assert.state(null != wxUser, "获取微信信息失败");
        Admin admin = adminService.getByAccount(account);
        Assert.state(null != admin, "用户或密码错误");
        adminService.bind(admin, wxUser);
        return admin;
    }


}
