package com.pony.test.mapper;

import com.pony.test.pojo.Fund;

import java.util.List;

public interface FundMapper {

    List<Fund> query(Long adminId);
}
