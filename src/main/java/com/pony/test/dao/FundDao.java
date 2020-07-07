package com.pony.test.dao;

import com.pony.test.pojo.Fund;

import java.util.List;

public interface FundDao {

    List<Fund> query(Long id);
}
