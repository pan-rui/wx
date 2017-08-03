package com.pc.wx.service;

import com.alibaba.fastjson.JSON;
import com.pc.wx.http.HttpUtil;
import com.pc.wx.po.BaseResult;
import com.pc.wx.po.ParamsMap;
import com.pc.wx.po.RemoteProtocol;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Description: ${Description}
 * @Author: 潘锐 (2017-07-17 17:14)
 * @version: \$Rev$
 * @UpdateAuthor: \$Author$
 * @UpdateDateTime: \$Date$
 */
@Service
//@Async
public class RemoteService {

    public Map getUserInfo(String ddBB, String openId, String tenantId) {
        return JSON.parseObject(HttpUtil.execute(RemoteProtocol.USERINFO, ParamsMap.newMap("openId", openId).addParams("ddBB",ddBB).addParams("tenantId",tenantId)), Map.class);
    }

    public Map validAndUser(String phone, String projectCode, String openId,Map<String,Object> wxUser) {
        return JSON.parseObject(HttpUtil.execute(RemoteProtocol.ValidAndUser, ParamsMap.newMap("openId", openId).addParams("projectCode", projectCode).addParams("phone", phone).addParams("wxUser",wxUser)));
    }

    public Map extInfo(String openId) {
        return JSON.parseObject(HttpUtil.execute(RemoteProtocol.EXT_INFOS, ParamsMap.newMap("openId", openId)));
    }
    public Map addUser(String openId,String tenantId,String projectCode,String ddBB,Map userInfo) {
        System.out.println("====wxUser=====>"+userInfo);
        Map<String,Object> resultMap=new LinkedHashMap();
        try {
            resultMap=JSON.parseObject(new String(HttpUtil.execute(RemoteProtocol.ADDUSER, ParamsMap.newMap("openId", openId).addParams("projectCode",projectCode).addParams("ddBB",ddBB).addParams("tenantId",tenantId).addParams("userInfo",userInfo)).getBytes("ISO-8859-1"),"UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return resultMap;
    }

    public Map getCheckWork(String openId, String tenantId, String ddBB, String projectCode, String month) {
        return JSON.parseObject(HttpUtil.execute(RemoteProtocol.CHECKWORK, ParamsMap.newMap("openId", openId).addParams("tenantId", tenantId).addParams("ddBB", ddBB).addParams("projectCode", projectCode).addParams("month", month)));
    }
    public Map getSalary(String openId, String tenantId, String ddBB, String projectCode) {
        return JSON.parseObject(HttpUtil.execute(RemoteProtocol.GETSALARY, ParamsMap.newMap("openId", openId).addParams("tenantId", tenantId).addParams("ddBB", ddBB).addParams("projectCode", projectCode)));  }

    public Map downImg(String openId, String tenantId, String ddBB, String projectCode,String serverId,int isFront,String accToken) {
        return JSON.parseObject(HttpUtil.execute(RemoteProtocol.DOWNIMG, ParamsMap.newMap("openId", openId).addParams("tenantId", tenantId).addParams("ddBB", ddBB).addParams("projectCode", projectCode).addParams("serverId",serverId).addParams("accToken",accToken).addParams("isFront",isFront)));
    }
}
