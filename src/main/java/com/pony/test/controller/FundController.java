package com.pony.test.controller;

import com.pony.test.service.impl.FundServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/fund")
public class FundController {

    @Autowired
    FundServiceImpl service;

    @RequestMapping("/get")
    public String get(Long id){
        return service.getProfit(id);
    }
}
