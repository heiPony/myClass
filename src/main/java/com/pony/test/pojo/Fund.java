package com.pony.test.pojo;

import java.math.BigDecimal;

public class Fund {

    private Long id;                //基金列表
    private Long adminId;           //归属人
    private String url;             //基金爬取数据url
    private BigDecimal money;       //持有金额
    private BigDecimal rate;        //实时净值估算
    private String divId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public String getDivId() {
        return divId;
    }

    public void setDivId(String divId) {
        this.divId = divId;
    }
}
