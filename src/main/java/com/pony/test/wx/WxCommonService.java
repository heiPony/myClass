package com.pony.test.wx;

import com.alibaba.fastjson.JSONObject;
import com.pony.test.constant.Constant;
import com.pony.test.redis.CacheKey;
import com.pony.test.redis.Redis;
import com.pony.test.utils.Assert;
import com.pony.test.utils.Result;
import com.pony.test.utils.ResultUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class WxCommonService {

	private Logger logger = LoggerFactory.getLogger(WxCommonService.class);
	
	//==========================query=================================
    
    /**
     * 	获取服务商小程序的sessionKey
     * xiesanchuan
     * @param code
     * @return
     * 2019年8月18日
     */
    public Result getSessionKeyByService(String code) {
    	logger.info("测试是否打包");
    	Assert.hasText(code, "CODE不能为空");
    	net.sf.json.JSONObject res = WxHttp.href(WxUtil.URL_PARAM.SP_CODE_SESSION, "GET", Constant.WX_SP.SERVICE_APPID, Constant.WX_SP.SERVICE_APPSECRET, code);
    	if(!res.containsKey("openid") && !res.containsKey("session_key")){
    		logger.warn("获取用户的SEESION_KEY失败,CODE:{},\t RESULT:{}", code, res.toString());
			return ResultUtils.returnError("获取用户的信息失败");
		}
    	logger.info("获取用户的SEESION_KEY, RESULT:{}", res.toString());
		String openId = res.getString("openid");
		String unionId = res.containsKey("unionid")?  res.getString("unionid") : "";
		String sessionKey = res.getString("session_key");
		toRedis(CacheKey.WX_KEY.SP_SERVICE_SESSION_KEY+openId, sessionKey);
		JSONObject result = new JSONObject();
		result.put("openId", openId);
		result.put("unionId", unionId);
    	return ResultUtils.returnSuccess(result);
	}



	/**
	 * 	存储用户的SESSION_KEY至redis
	 * xiesanchuan
	 * @param res
	 * 2019年8月18日
	 */
	private void toRedis(String key, String sessionKey) {
		Redis.getInstance().set(key, sessionKey);
		Redis.getInstance().expire(key, CacheKey.EXPIRE_TIME.FIVE_MIN);
	}

	public WxUser getUser(String sessionKey, String iv, String encryptedData) {
		net.sf.json.JSONObject result = WxUtil.getUserInfo(encryptedData, sessionKey, iv);
		logger.info("result,{}",JSONObject.toJSONString(result));
		if (null == result) {
			return null;
		}
		logger.info("解密结果RESULT:{}", result.toString());
		return getUser(result);

	}
	public WxUser getUser(net.sf.json.JSONObject result){
		WxUser wxUser = new WxUser();
		wxUser.setOpenId(result.getString("openId"));
		wxUser.setNickName(result.getString("nickName"));
		wxUser.setHeadImgUrl(result.getString("avatarUrl"));
		wxUser.setWxSex(result.getInt("gender"));
		wxUser.setProvince(result.getString("province"));
		wxUser.setCountry(result.getString("country"));
		return wxUser;
	}

}
