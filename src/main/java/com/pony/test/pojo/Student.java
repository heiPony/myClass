package com.pony.test.pojo;

import com.alibaba.fastjson.annotation.JSONField;
import com.pony.test.utils.DateUtils;

import java.util.Date;

public class Student {


    private Long id;
    private String name;
    private String stuNumber;
    private String grade;
    private Byte classId;
    @JSONField(format = DateUtils.format_yyyyMMddHHmmss)
    private Date createTime;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStuNumber() {
        return stuNumber;
    }

    public void setStuNumber(String stuNumber) {
        this.stuNumber = stuNumber;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public Byte getClassId() {
        return classId;
    }

    public void setClassId(Byte classId) {
        this.classId = classId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
