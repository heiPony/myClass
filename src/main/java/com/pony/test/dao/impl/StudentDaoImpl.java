package com.pony.test.dao.impl;


import com.pony.test.dao.StudentDao;
import com.pony.test.mapper.StudentMapper;
import com.pony.test.pojo.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class StudentDaoImpl implements StudentDao {

    @Autowired
    StudentMapper mapper;
    @Override
    public Student get(String stuNumber) {
        return mapper.get(stuNumber);
    }
}
