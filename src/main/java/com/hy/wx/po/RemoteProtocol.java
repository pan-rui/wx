package com.hy.wx.po;

import org.springframework.http.HttpMethod;

import java.util.Map;

/**
 * 116.7.226.222:100
 * @Description: ${Description}
 * @Author: 潘锐 (2017-07-17 16:37)
 * @version: \$Rev$
 * @UpdateAuthor: \$Author$
 * @UpdateDateTime: \$Date$
 */
public enum RemoteProtocol implements Protocol{
    USERINFO("http://58.61.142.74:1000/consumer/userInfo?openId=openId",FORM, HttpMethod.GET)
    ,EXT_INFOS("http://116.7.226.222:10001/weChat/extInfos",FORM,HttpMethod.POST,ParamsMap.newMap("openId",""))
    , ValidAndUser("http://116.7.226.222:10001/weChat/validAndUser", JSON, HttpMethod.POST, ParamsMap.newMap("openId", "").addParams("phone", "").addParams("projectCode", "").addParams("wxUser", ""))
    ,ADDUSER("http://116.7.226.222:10001/weChat/addUser",JSON,HttpMethod.POST,ParamsMap.newMap("openId","").addParams("userInfo","").addParams("tenantId","").addParams("ddBB","").addParams("projectCode",""))
//    ,ADDWXUSER("http://116.7.226.222:10001/weChat/addWxUser",JSON,HttpMethod.POST,ParamsMap.newMap("openId","").addParams("userInfo","").addParams("tenantId",""))
    ,CHECKWORK("http://116.7.226.222:10001/weChat/getCheckWork",FORM,HttpMethod.POST,ParamsMap.newMap("openId","").addParams("ddBB","").addParams("tenantId","").addParams("month","").addParams("projectCode",""))
    ,GETSALARY("http://116.7.226.222:10001/weChat/getSalary",FORM,HttpMethod.POST,ParamsMap.newMap("openId","").addParams("ddBB","").addParams("tenantId","").addParams("projectCode",""))
    ,DOWNIMG("http://116.7.226.222:10001/weChat/downImg",FORM,HttpMethod.POST,ParamsMap.newMap("openId","").addParams("ddBB","").addParams("tenantId","").addParams("projectCode","").addParams("serverId","").addParams("isFront","").addParams("accToken",""))
    , ALINOTIFY("http://58.61.142.74:1000/consumer/order/aliPayNotify", JSON, HttpMethod.POST)
//    , ALINOTIFY("http://192.168.3.25:8080/consumer/order/aliPayNotify", JSON, HttpMethod.POST)
    , WXNOTIFY("http://116.7.226.222:10001/weChat/validAndUser", JSON, HttpMethod.POST)
    , USER_EDIT("http://58.61.142.74:1000/consumer/edit", JSON, HttpMethod.POST)

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

    RemoteProtocol(String url, HttpMethod method) {
        this.url=url;
        this.method=method;
    }
    RemoteProtocol(String url,String contentType, HttpMethod method) {
        this.url=url;
        this.contentType=contentType;
        this.method=method;
    }
    RemoteProtocol(String url, String contentType,HttpMethod method, Map<String, Object> parmas) {
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
