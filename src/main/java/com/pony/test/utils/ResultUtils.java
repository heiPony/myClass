package com.pony.test.utils;


import javax.validation.ConstraintViolation;
import java.util.Set;

public class ResultUtils {
    public ResultUtils() {
    }

    public static Result returnError(String msg) {
        Result result = new Result();
        result.setCode(0);
        result.setInfo(msg);
        result.setSuccess(false);
        return result;
    }

    public static <T> Result<T> returnError(String msg, T unwrappData) {
        Result result = new Result();
        result.setCode(0);
        result.setInfo(msg);
        result.setData(unwrappData);
        result.setSuccess(false);
        return result;
    }

    public static Result returnError(String msg, Integer code) {
        Result result = new Result();
        result.setCode(code);
        result.setInfo(msg);
        result.setSuccess(false);
        return result;
    }

    public static <T> Result<T> returnError(Set<ConstraintViolation<T>> constraintViolations) {
        Result result = new Result();
        result.setCode(0);
        result.setInfo(((ConstraintViolation)constraintViolations.iterator().next()).getMessage());
        result.setSuccess(false);
        return result;
    }

    public static Result returnSuccess(String msg) {
        Result result = new Result();
        result.setCode(1);
        result.setInfo(msg);
        result.setSuccess(true);
        return result;
    }

    public static <T> Result<T> returnSuccess(String msg, T object) {
        Result result = new Result();
        result.setCode(1);
        result.setInfo(msg);
        result.setData(object);
        result.setSuccess(true);
        return result;
    }

    public static <T> Result<T> returnSuccess(T object) {
        Result result = new Result();
        result.setCode(1);
        result.setInfo("");
        result.setData(object);
        result.setSuccess(true);
        return result;
    }
}
