package com.pc.wx.service;

import com.alibaba.fastjson.JSON;
import com.pc.wx.aes.AesException;
import com.pc.wx.aes.SHA1;
import com.pc.wx.aes.WxBizMsgCrypt;
import com.pc.wx.aes.XMLParse;
import com.pc.wx.http.CacheUtil;
import com.pc.wx.http.HttpUtil;
import com.pc.wx.po.BaseResult;
import com.pc.wx.po.Msg;
import com.pc.wx.po.ParamsMap;
import com.pc.wx.po.RemoteProtocol;
import com.pc.wx.po.WxProtocol;
import com.sun.imageio.plugins.common.ImageUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
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
    @Autowired
    private WxBizMsgCrypt wxBizMsgCrypt;

    public String getToken() {
        String token=cacheUtil.getCache(appId);
        if(StringUtils.isEmpty(token)) {
            String resultStr = HttpUtil.execute(WxProtocol.TOKEN, ParamsMap.newMap("grant_type", "client_credential").addParams("appid", appId).addParams("secret", appsecret));
            Map<String, Object> result = JSON.parseObject(resultStr, Map.class);
            token=result.get("access_token").toString();
            cacheUtil.setCacheOnExpire(appId,token , Integer.parseInt(result.get("expires_in").toString()) - 30);
        }
        return token;
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
                    Map<String,Object> refTokenMap = JSON.parseObject(HttpUtil.execute(WxProtocol.REFRESH_TOKEN, ParamsMap.newMap("appid", appId).addParams("grant_type", "refresh_token").addParams("refresh_token", refToken)));
                        System.out.println("===>"+refTokenMap);
                    if(refTokenMap.get("errCode").equals("0")) {
                        token = (String) refTokenMap.get("access_token");
                        cacheUtil.setCacheOnExpire((String) refTokenMap.get("openid"), token, Integer.parseInt(refTokenMap.get("expires_in").toString()) - 30);
                        cacheUtil.setCacheOnExpire((String) refTokenMap.get("openid") + "_up", (String) refTokenMap.get("refresh_token"), 3600 * 24 * 30 - 30);
                    }else isQuery=true;
                }else isQuery=true;
            }
        }
        if (isQuery) {
            try {
                String atStr = HttpUtil.execute(WxProtocol.CODE, ParamsMap.newMap("appid", appId).addParams("redirect_uri", URLEncoder.encode(redirect_uri, "UTF-8")).addParams("response_type", response_type).addParams("scope", scope).addParams("state", "234567"));
                if (!atStr.startsWith("{"))
                    return atStr;
                Map<String, Object> result = JSON.parseObject(atStr, Map.class);
                System.out.println("=============refresh_token");
                String tokenStr = HttpUtil.execute(WxProtocol.ACCESS_TOKEN, ParamsMap.newMap("appid", appId).addParams("secret", appsecret).addParams("code", result.get("code")).addParams("grant_type", "authorization_code"));
                if (!atStr.startsWith("{")) return tokenStr;
                Map<String, Object> tokenMap = JSON.parseObject(tokenStr, Map.class);
                token = (String) tokenMap.get("access_token");
                cacheUtil.setCacheOnExpire((String) tokenMap.get("openid"), token, Integer.parseInt(tokenMap.get("expires_in").toString())-30);
                cacheUtil.setCacheOnExpire((String) tokenMap.get("openid")+"_up", (String)tokenMap.get("refresh_token"), 3600*24*30-30);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return token;
    }

    public Map respToken(String code, String state) {
        System.out.println("code==>"+code+"        state===>"+state);
        String tokenStr = HttpUtil.execute(WxProtocol.ACCESS_TOKEN, ParamsMap.newMap("appid", appId).addParams("secret", appsecret).addParams("code", code).addParams("grant_type", "authorization_code"));
        if (!tokenStr.startsWith("{")) return ParamsMap.newMap("301","后端异常");
        ParamsMap<String, Object> tokenMap = JSON.parseObject(tokenStr, ParamsMap.class);
        token = (String) tokenMap.get("access_token");
        String openid=(String) tokenMap.get("openid");
        System.out.println("回调返回==>"+tokenMap);
        cacheUtil.setCacheOnExpire(state,openid,300);
        cacheUtil.setCacheOnExpire(openid, token, Integer.parseInt(tokenMap.get("expires_in").toString())-30);      //access_token
        cacheUtil.setCacheOnExpire(openid+"_up", (String)tokenMap.get("refresh_token"), 3600*24*30-30);     //refresh_token
        //后端查询
        Map<String,Object> resultMap=JSON.parseObject(HttpUtil.execute(RemoteProtocol.USERINFO,ParamsMap.newMap("openId",openid)),Map.class);
        resultMap.put("openId", openid);
        if((int)resultMap.get("code") !=0)
            return tokenMap;
        else{
            cacheUtil.hSetCache("USERINFO",openid,JSON.toJSONString(resultMap.get("data")));
            return resultMap;
        }
    }

    public ParamsMap createMenu(String aToken,List<Map> menuList) {
        ParamsMap menuMap = ParamsMap.newMap("button", menuList).addParams("access_token", aToken);
        String result=HttpUtil.execute(WxProtocol.CREATE_MENU, menuMap);
        return JSON.parseObject(result, ParamsMap.class);
    }

    public String inMsg(String signature, String timestamp, String nonce, String openid, String encrypt_type, String msg_signature, String msg) {
        try {
            System.out.println("接收到=========>\r\n"+msg);
            String decryptMsg=wxBizMsgCrypt.decryptMsg(msg_signature, timestamp, nonce, msg);
            System.out.println("解密后==>\r\n"+decryptMsg);
//            cacheUtil.setCache(openid+"_t","signature="+signature+"#nonce="+nonce+"#timestamp="+timestamp);
//            String token=reqToken(openid);
            return wxBizMsgCrypt.encryptMsg(token, timestamp, nonce);
        } catch (AesException e) {
            e.printStackTrace();
        }
        return "";
    }

    public Map<String, Object> getHead(String nostr) {
        String openId = cacheUtil.getCache(nostr);
        Map<String, Object> headInfo = new HashMap();
        if (openId != null) {
            headInfo.put("openId", openId);
            headInfo.put("tenantId", cacheUtil.hGetCache("TENANTID", openId));
            headInfo.put("projectId", cacheUtil.hGetCache("PROJECTCODE", openId));
            headInfo.put("ddBB", cacheUtil.hGetCache("DDBB", openId));
        }
        return headInfo;
    }

    public Map<String, Object> getHeadForOid(String openId) {
        Map<String, Object> headInfo = new HashMap();
        headInfo.put("openId", openId);
        headInfo.put("tenantId", cacheUtil.hGetCache("TENANTID", openId));
        headInfo.put("projectId", cacheUtil.hGetCache("PROJECTCODE", openId));
        headInfo.put("ddBB", cacheUtil.hGetCache("DDBB", openId));
        return headInfo;
    }

    public Map downImg(String openId, String media) {
        return JSON.parseObject(HttpUtil.execute(WxProtocol.DOWNIMG, ParamsMap.newMap("openId", openId).addParams("media_id", media)));
    }

    public Map<String, Object> getWxUser(String openId) {
        Map<String,Object> wxUser= new LinkedHashMap();
        try {
            wxUser = JSON.parseObject(new String(HttpUtil.execute(WxProtocol.GETUSER, ParamsMap.newMap("access_token",getToken()).addParams("openid",openId).addParams("lang","zh_CN")).getBytes("ISO-8859-1"),"UTF-8"),Map.class);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return wxUser;
    }

/*    public Map<String, Object> getUserInfo(String openId) {
        Map<String, Object> userInfo = JSON.parseObject(cacheUtil.hGetCache("USERINFO", openId), Map.class);
        if (CollectionUtils.isEmpty(userInfo)) {
            userInfo = JSON.parseObject(HttpUtil.execute(RemoteProtocol.USERINFO, ParamsMap.newMap("openId", openId)), Map.class);
        }
        return userInfo;
    }*/
}
