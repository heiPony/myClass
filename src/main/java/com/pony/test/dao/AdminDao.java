package com.pony.test.dao;

import com.pony.test.pojo.Admin;

public interface AdminDao {
    Admin get(String name);
    Admin getByAccount(String account);

    Admin getByOpenId(String openId);

    void bind(Admin admin);

    Admin getByStudent(Long id);

}
