package com.pony.test.api;


import com.pony.test.service.impl.AdminServiceImpl;
import com.pony.test.service.impl.LoginServiceImpl;
import com.pony.test.utils.Assert;
import com.pony.test.utils.Result;
import com.pony.test.utils.ResultUtils;
import com.pony.test.wx.WxCommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sp/login")
public class LoginApi {


    private Logger logger = LoggerFactory.getLogger(LoginApi.class);
    @Autowired
    AdminServiceImpl service;
    @Autowired
    LoginServiceImpl loginService;

    @Autowired
    WxCommonService wxCommonService;

    /**
     * 
     * @Author huangzhanping
     * @param [code]
     * @return 2020/6/11 15:45
     */
    @RequestMapping(value = "/get-session-key", method = RequestMethod.POST)
    public Result getSessionKey(String code){
        return wxCommonService.getSessionKeyByService(code);
    }


    @RequestMapping("/bind")
    public Result login(String account, String password, String openId, String iv, String encryptedData) {
        logger.info("开始验证信息");
        logger.info("account:{},/t password:{},/t openId:{},/t iv:{},/t encryptedData:{},/t", account, password, openId, iv, encryptedData);
        Assert.hasLength(account, "账号不能为空");
        Assert.hasLength(password, "密码不能为空");
        Assert.hasLength(openId, "openId不能为空");
        Assert.hasLength(iv, "iv不能为空");
        Assert.hasLength(encryptedData, "encryptedData不能为空");
        return ResultUtils.returnSuccess(loginService.login(account, password, openId, iv, encryptedData));
    }

    @RequestMapping("/add")
    public Result add(String phone, String stuNumber, String password){
        logger.info("注册学生账号");
        Assert.notNull(stuNumber, "请填写学号");
        Assert.notNull(phone, "请填写手机号码");
        Assert.notNull(password, "请填写密码");
        return ResultUtils.returnSuccess("注册成功");
    }


    @RequestMapping("/get-code")
    public Result getCode(String phone){
        logger.info("注册--获取验证");
        Assert.notNull(phone, "请填写手机号码");

        return ResultUtils.returnSuccess("注册成功");
    }



}
