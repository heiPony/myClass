package com.pony.test.controller;

import com.pony.test.service.AdminService;
import com.pony.test.service.impl.AdminServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class TestController {

    private Logger logger = LoggerFactory.getLogger(TestController.class);

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    AdminServiceImpl service;


    @RequestMapping("/admin")
    public Map list(){
        List<Map<String, Object>> maps = jdbcTemplate.queryForList("select * from admin");
        Map<String, Object> map = maps.get(0);
        logger.info("测试jenkins");
        return map;
    }

    @RequestMapping("/count")
    public String test(){
        return service.count("1206012116");
    }

    @RequestMapping("/test")
    public String jenkins(){
        return service.count("jenkins");
    }
}
