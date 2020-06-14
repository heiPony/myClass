package com.pony.test.utils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
@ApiModel("响应")
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 262888242269244717L;
    @ApiModelProperty(
            value = "响应代码",
            notes = "状态，0为失败，1为成功，其他自定义"
    )
    private Integer code = 1;
    @ApiModelProperty("提示信息")
    private String info;
    @ApiModelProperty("响应数据")
    private T data;
    @ApiModelProperty("响应是否成功")
    private boolean success;

    public Result() {
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Integer getCode() {
        return this.code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getInfo() {
        return this.info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public static long getSerialVersionUID() {
        return 262888242269244717L;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}


