package com.pc.wx.po;

import com.pc.wx.http.HttpUtil;
import org.springframework.http.HttpMethod;

import java.util.Map;

/**
 * @Description: ${Description}
 * @Author: 潘锐 (2017-07-17 16:37)
 * @version: \$Rev$
 * @UpdateAuthor: \$Author$
 * @UpdateDateTime: \$Date$
 */
public enum WxProtocol {
    TOKEN("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET", HttpMethod.GET)
,CODE("https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect",HttpMethod.GET)        //TODO:wechat_redirect
    ,ACCESS_TOKEN("https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code", HttpMethod.GET)
    ,REFRESH_TOKEN("https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=APPID&grant_type=refresh_token&refresh_token=REFRESH_TOKEN",HttpMethod.GET)
    ;
    private String url;
    private HttpMethod method;
    private Map<String,Object> postParams;

    public String getUrl() {
        return url;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public Map<String, Object> getPostParams() {
        return postParams;
    }

    WxProtocol(String url, HttpMethod method) {
        this.url=url;
        this.method=method;
    }
    WxProtocol(String url, HttpMethod method, Map<String, Object> parmas) {
        this.url=url;
        this.method=method;
        this.postParams=parmas;
    }
}
