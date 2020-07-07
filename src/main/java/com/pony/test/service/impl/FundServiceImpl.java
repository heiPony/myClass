package com.pony.test.service.impl;

import com.pony.test.dao.impl.FundDaoImpl;
import com.pony.test.pojo.Fund;
import com.pony.test.service.FundService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.codec.binary.Base64;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Service
public class FundServiceImpl implements FundService {

    private static Logger logger = LoggerFactory.getLogger(FundServiceImpl.class);
    @Autowired
    FundDaoImpl dao;

    @Override
    public String getProfit(Long adminId) {
        List<Fund> list = dao.query(adminId);
        if(CollectionUtils.isEmpty(list)){
            return "0";
        }
        BigDecimal sum = new BigDecimal(0);
        for (Fund item : list) {
            if(StringUtils.isBlank(item.getUrl()) || StringUtils.isBlank(item.getDivId())){
                continue;
            }
            BigDecimal rate = geRate(item.getUrl(), item.getDivId());
            sum = sum.add(item.getMoney().multiply(rate)).setScale(2,BigDecimal.ROUND_HALF_UP);
        }

        return new BigDecimal("0.01").multiply(sum).toString();
    }



    public static void main(String[] args) {
        String url = "http://fund.eastmoney.com/008887.html?spm=aladin";
        String eId = "gz_gszzl";

    }


    public  BigDecimal geRate(String dataUrl, String eId) {
        Document doc = null;
        try {
            doc = Jsoup.connect(dataUrl).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String rateStr = doc.getElementById(eId).html();
        rateStr = rateStr.substring(0, rateStr.indexOf("%"));
        return new BigDecimal(rateStr);
    }


}
