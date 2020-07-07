package com.pony.test.mapper;

import com.pony.test.pojo.Admin;

import java.util.List;

public interface AdminMapper {

    Admin get(String name);

    Admin getByAccount(String account);

    List<Admin> query();

    Admin getByOpenId(String openId);

    void update(Long id, String openId);

    void bind(Admin admin);

    Admin getByStudent(Long id);

    Admin get(Long id);
}
