package com.pony.test.dao;

import com.pony.test.mapper.AdminMapper;
import com.pony.test.pojo.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AdminDaoImpl implements AdminDao {

    @Autowired
    private AdminMapper mapper;
    @Override
    public Admin get(String name) {
        return mapper.get(name);
    }

    @Override
    public Admin getByAccount(String account) {
        return mapper.getByAccount(account);
    }


    @Override
    public Admin getByOpenId(String openId) {
        return mapper.getByOpenId(openId);
    }

    @Override
    public void bind(Admin admin) {
        mapper.bind(admin);
    }


}
