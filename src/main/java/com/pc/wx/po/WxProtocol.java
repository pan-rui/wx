package com.pc.wx.po;

import org.springframework.http.HttpMethod;

import java.util.Map;

/**
 * @Description: ${Description}
 * @Author: 潘锐 (2017-07-17 16:37)
 * @version: \$Rev$
 * @UpdateAuthor: \$Author$
 * @UpdateDateTime: \$Date$
 */
public enum WxProtocol implements Protocol {
    TOKEN("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET",FORM, HttpMethod.GET)
,CODE("https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect",FORM,HttpMethod.GET)        //TODO:wechat_redirect
    ,ACCESS_TOKEN("https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code",FORM, HttpMethod.GET)
    ,REFRESH_TOKEN("https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=APPID&grant_type=refresh_token&refresh_token=REFRESH_TOKEN",FORM,HttpMethod.GET)
    ,CREATE_MENU("https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN",JSON,HttpMethod.POST)
    ,JSTICKET("https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi",FORM,HttpMethod.GET)
    ,GETUSER("https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN",FORM,HttpMethod.GET)
    ,DOWNIMG("http://file.api.weixin.qq.com/cgi-bin/media/get?access_token=ACCESS_TOKEN&media_id=MEDIA_ID",BYTE,HttpMethod.GET)
    ;
    private String url;
    private String contentType;
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

    WxProtocol(String url,String contentType, HttpMethod method) {
        this.url=url;
        this.contentType=contentType;
        this.method=method;
    }
    WxProtocol(String url,String contentType, HttpMethod method, Map<String, Object> parmas) {
        this.url=url;
        this.contentType=contentType;
        this.method=method;
        this.postParams=parmas;
    }

    @Override
    public String getContentType() {
        return contentType;
    }
}
