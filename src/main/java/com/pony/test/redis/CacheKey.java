package com.pony.test.redis;

public class CacheKey {

    public interface WX_KEY{
        String COMPNONENT_ACCESS_TOKEN = "WX:COMPNONENT:ACCESS_TOKEN";
        String BONUS_ACCESS_TOKEN = "WX:BONUX:ACCESS_TOKEN";
        //============服务商小程序key=============
        String SP_SERVICE_ACCESS_TOKEN = "WX:SP:SERVICE:ACCESS_TOKEN";
        String SP_SERVICE_JS_TICKET = "WX:SP:SERVICE:JS_TICKET";
        String SP_SERVICE_SESSION_KEY = "WX:SP:SERVICE:SESSION_KEY:";
        //=============工厂小程序key===============
        String SP_FACTORY_ACCESS_TOKEN = "WX:SP:FACTORY:ACCESS_TOKEN";
        String SP_FACTORY_JS_TICKET = "WX:SP:FACTORY:JS_TICKET";
        String SP_FACTORY_SESSION_KEY = "WX:SP:FACTORY:SESSION_KEY:";
        //=============城市分类小程序key===============
        String SP_CITY_ACCESS_TOKEN = "WX:SP:CITY:ACCESS_TOKEN";
        String SP_CITY_JS_TICKET = "WX:SP:CITY:JS_TICKET";
        String SP_CITY_SESSION_KEY = "WX:SP:CITY:SESSION_KEY:";

        //===============云纸主号=======================
        String MP_MAIN_ACCESS_TOKEN = "WX:MP:MAIN_ACCESS_TOKEN";			//云纸授权主号的accessToken
        String MP_PAY_ACCESS_TOKEN = "WX:MP:PAY_ACCESS_TOKEN";				//云纸授权付费主号的accessToken
        String MP_MAIN_JS_API_TICKET = "WX:MP:MAIN_JS_API_TICKET";			//云纸授权主要的jsTicket
        String MP_AUTH_ACCESS_TOKEN = "WX:MP:AUTH_ACCESS_TOKEN";			//云纸授权公众号的accessToken
        String MP_AUTH_JS_API_TICKET = "WX:MP:AUTH_JS_API_TICKET";			//云纸授权公众号的jsTicket
    }

    public interface EXPIRE_TIME{
        int FIVE_MIN = 5*60;
    }
}
