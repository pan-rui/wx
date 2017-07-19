package com.pc.wx.service;

import com.alibaba.fastjson.JSON;
import com.pc.wx.http.CacheUtil;
import com.pc.wx.http.HttpUtil;
import com.pc.wx.po.ParamsMap;
import com.pc.wx.po.WxProtocol;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * @Description: ${Description}
 * @Author: 潘锐 (2017-07-17 17:14)
 * @version: \$Rev$
 * @UpdateAuthor: \$Author$
 * @UpdateDateTime: \$Date$
 */
@Service
public class WxService {
    @Value("#{config['appId']}")
    private String appId;
    @Value("#{config['appsecret']}")
    private String appsecret;
    @Value("#{config['token']}")
    private String token;
    @Value("#{config['EncodingAESKey']}")
    private String EncodingAESKey;
    @Value("#{config['redirect_uri']}")
    private String redirect_uri;
    @Value("#{config['scope']}")
    private String scope;
    private String response_type = "code";
    @Autowired
    private CacheUtil cacheUtil;

    public String getToken() {
        return HttpUtil.execute(WxProtocol.TOKEN, ParamsMap.newMap("grant_type", "client_credential").addParams("appid", appId).addParams("secret", appsecret));
    }

    public String reqToken(String openId) {
        String token = "";
        boolean isQuery=false;
        if (StringUtils.isEmpty(openId)) {
            isQuery=true;
        } else {
            token = cacheUtil.getCache(openId);
            if(StringUtils.isEmpty(token)){
                String refToken=cacheUtil.getCache(openId+"_up");
                if(!StringUtils.isEmpty(refToken)) {
                    String refStr = HttpUtil.execute(WxProtocol.REFRESH_TOKEN, ParamsMap.newMap("appid", appId).addParams("grant_type", "grant_type").addParams("refresh_token", refToken));
                    if(refStr.startsWith("{")) {
                        Map<String, Object> refMap = JSON.parseObject(refStr, Map.class);
                        token = (String) refMap.get("access_token");
                        cacheUtil.setCacheOnExpire((String) refMap.get("openid"), token, Integer.parseInt(refMap.get("expires_in").toString()) - 30);
                        cacheUtil.setCacheOnExpire((String) refMap.get("openid") + "_up", (String) refMap.get("refresh_token"), 3600 * 24 * 30 - 30);
                    }else return refStr;
                }else isQuery=true;
            }
        }
        if (isQuery) {
            try {
                String atStr = HttpUtil.execute(WxProtocol.CODE, ParamsMap.newMap("appid", appId).addParams("redirect_uri", URLEncoder.encode(redirect_uri, "UTF-8")).addParams("response_type", response_type).addParams("scope", scope).addParams("state", "234567"));
/*                if (!atStr.startsWith("{"))
                    return atStr;
                Map<String, Object> result = JSON.parseObject(atStr, Map.class);
                String tokenStr = HttpUtil.execute(WxProtocol.ACCESS_TOKEN, ParamsMap.newMap("appid", appId).addParams("secret", appsecret).addParams("code", result.get("code")).addParams("grant_type", "authorization_code"));
                if (!atStr.startsWith("{")) return tokenStr;
                Map<String, Object> tokenMap = JSON.parseObject(tokenStr, Map.class);
                token = (String) tokenMap.get("access_token");
                cacheUtil.setCacheOnExpire((String) tokenMap.get("openid"), token, Integer.parseInt(tokenMap.get("expires_in").toString())-30);
                cacheUtil.setCacheOnExpire((String) tokenMap.get("openid")+"_up", (String)tokenMap.get("refresh_token"), 3600*24*30-30);*/
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return token;
    }

    public String respToken(String code, String state) {
        String tokenStr = HttpUtil.execute(WxProtocol.ACCESS_TOKEN, ParamsMap.newMap("appid", appId).addParams("secret", appsecret).addParams("code", code).addParams("grant_type", "authorization_code"));
        if (!tokenStr.startsWith("{")) return tokenStr;
        Map<String, Object> tokenMap = JSON.parseObject(tokenStr, Map.class);
        token = (String) tokenMap.get("access_token");
        cacheUtil.setCacheOnExpire((String) tokenMap.get("openid"), token, Integer.parseInt(tokenMap.get("expires_in").toString())-30);
        cacheUtil.setCacheOnExpire((String) tokenMap.get("openid")+"_up", (String)tokenMap.get("refresh_token"), 3600*24*30-30);
        return "SUCCESS";
    }
}
