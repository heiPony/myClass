package com.pony.test.dao.impl;

import com.pony.test.dao.FundDao;
import com.pony.test.mapper.FundMapper;
import com.pony.test.pojo.Fund;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class FundDaoImpl implements FundDao {

    @Autowired
    FundMapper mapper;

    @Override
    public List<Fund> query(Long adminId) {
        return mapper.query(adminId);
    }
}
