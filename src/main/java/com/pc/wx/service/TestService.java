package com.pc.wx.service;

import com.pc.wx.http.HttpUtil;
import com.pc.wx.po.ParamsMap;
import com.pc.wx.po.RemoteProtocol;

/**
 * @Description: ${Description}
 * @Author: 潘锐 (2017-07-25 18:30)
 * @version: \$Rev$
 * @UpdateAuthor: \$Author$
 * @UpdateDateTime: \$Date$
 */
public class TestService {
    public static void main(String[] args) {
        String str=HttpUtil.execute(RemoteProtocol.ValidAndUser, ParamsMap.newMap("openId","opUEX1V3ssibCUAtfxP7GocJAxyk").addParams("phone","17666128822").addParams("projectCode","44030120170531001"));
        System.out.println(str);
    }
}
