package com.pony.test.wx;

import net.sf.json.JSONObject;
import org.springframework.util.StringUtils;

/**
 * 微信用户对象
 * @author linweiqin
 * @Date 2014-11-14 下午2:22:56
 */
public class WxUser {

    public WxUser() {
        super();
    }

    public WxUser(JSONObject jsonUser) {
        try {
            if (jsonUser.get("errcode") != null) {
                return;
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        openId = jsonUser.getString("openid");
        nickName = jsonUser.containsKey("nickname") ? this.unicodStr(jsonUser.getString("nickname")) : "";
        if (StringUtils.isEmpty(nickName)) {
            nickName = "dp";
        }
        wxSex = jsonUser.containsKey("sex") ? Integer.parseInt(jsonUser.getString("sex")) : 0;
        switch (wxSex){
            case 1:
                sex = "男";
                break;
            case 2:
                sex = "女";
                break;
            default:
                sex = "未知";
                break;
        }
        province = jsonUser.containsKey("province") ? jsonUser.getString("province") : "";
        city = jsonUser.containsKey("city") ? jsonUser.getString("city") : "";
        country = jsonUser.containsKey("country") ? jsonUser.getString("country") : "";
        headImgUrl = jsonUser.containsKey("headimgurl") ? jsonUser.getString("headimgurl") : "";
        subscribe = jsonUser.containsKey("subscribe") ? jsonUser.getInt("subscribe") : null;
        // privilege = ????;
        unionId = !jsonUser.containsKey("unionid") ? "" : jsonUser.getString("unionid");
    }

    public String unicodStr(String str) {
        char[] chars = str.toCharArray();
        StringBuffer buff = new StringBuffer("");
        for (int i = 0; i < chars.length; i++) {
            if ((chars[i] >= 19968 && chars[i] <= 40869) || (chars[i] >= 97 && chars[i] <= 122)
                    || (chars[i] >= 65 && chars[i] <= 90)) {
                buff.append(chars[i]);
            }
        }
        return buff.toString();
    }

    private Integer wxSex;
    private String openId; // 用户的唯一标识
    private String unionId; // 用户的唯一标识
    private String nickName; // 用户昵称
    private String sex; // 用户的性别，值为1时是男性，值为2时是女性，值为0时是未知
    private String province; // 用户个人资料填写的省份
    private String city; // 普通用户个人资料填写的城市
    private String country; // 国家，如中国为CN
    private String headImgUrl; // 用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），用户没有头像时该项为空
    // private String[] privilege; // 用户特权信息，json 数组，如微信沃卡用户为（chinaunicom）
    // private String unionid; // 只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段。
    private Integer subscribe;// 关注属性

    public Integer getSubscribe() {
        return subscribe;
    }

    public void setSubscribe(Integer subscribe) {
        this.subscribe = subscribe;
    }

    public String getOpenId() {
        return openId;
    }

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

    public Integer getWxSex() {
        return wxSex;
    }

    public void setWxSex(Integer wxSex) {
        this.wxSex = wxSex;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }
}
