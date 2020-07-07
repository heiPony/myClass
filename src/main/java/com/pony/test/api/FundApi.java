package com.pony.test.api;

import com.pony.test.pojo.Admin;
import com.pony.test.service.impl.AdminServiceImpl;
import com.pony.test.service.impl.FundServiceImpl;
import com.pony.test.utils.Assert;
import com.pony.test.utils.Result;
import com.pony.test.utils.ResultUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/sp/fund")
public class FundApi {

    private Logger logger = LoggerFactory.getLogger(FundApi.class);

    @Autowired
    FundServiceImpl service;
    @Autowired
    AdminServiceImpl adminService;
    @RequestMapping("/get-profit")
    public Result getCode(Long adminId){
        Assert.notNull(adminId, "请登录");
        Admin admin = new Admin();
        admin.setProfit(service.getProfit(adminId));
        return ResultUtils.returnSuccess(admin);
    }

}
