package com.pony.test.constant;

import java.util.ResourceBundle;

public class Constant {


    public static ResourceBundle rb = ResourceBundle.getBundle("application");
    public static String SUPPER_PRE = "SUPPER_";
    public static String SUPPER_PWD = "yunZhi@123";

    public interface WX_SP {
        String SERVICE_APPID = rb.getString("wx.sp.service.appid");
        String SERVICE_APPSECRET = rb.getString("wx.sp.service.appsecret");
    }

}
