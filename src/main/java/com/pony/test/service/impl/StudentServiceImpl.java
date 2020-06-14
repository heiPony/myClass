package com.pony.test.service.impl;

import com.pony.test.dao.impl.StudentDaoImpl;
import com.pony.test.pojo.Student;
import com.pony.test.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class StudentServiceImpl implements StudentService {


    @Autowired
    StudentDaoImpl dao;


    @Override
    public Student get(String stuNumber) {
        return dao.get(stuNumber);
    }
}
