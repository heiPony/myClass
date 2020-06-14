package com.pony.test.controller;

import com.pony.test.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class TestController {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    AdminService service;

    @RequestMapping("/admin")
    public Map list(){
        List<Map<String, Object>> maps = jdbcTemplate.queryForList("select * from admin");
        Map<String, Object> map = maps.get(0);
        return map;
    }




}
