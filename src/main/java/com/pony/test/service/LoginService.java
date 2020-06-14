package com.pony.test.service;

import com.pony.test.pojo.Admin;

public interface LoginService {



    Admin login(String account, String password, String openId, String iv, String encryptedData);
}
