package com.pony.test.dao.impl;


import com.pony.test.dao.StudentDao;
import com.pony.test.mapper.StudentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class StudentDaoImpl implements StudentDao {

    @Autowired
    StudentMapper mapper;
    @Override
    public int count(String stuNumber) {
        return mapper.count(stuNumber);
    }
}
