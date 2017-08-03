package com.pc.wx.service;

import com.pc.wx.http.HttpUtil;
import com.pc.wx.po.ParamsMap;
import com.pc.wx.po.RemoteProtocol;
import org.springframework.util.StringUtils;

/**
 * @Description: ${Description}
 * @Author: 潘锐 (2017-07-25 18:30)
 * @version: \$Rev$
 * @UpdateAuthor: \$Author$
 * @UpdateDateTime: \$Date$
 */
public class TestService {
    public static void main2(String[] args) {
        String str=HttpUtil.execute(RemoteProtocol.ValidAndUser, ParamsMap.newMap("openId","opUEX1V3ssibCUAtfxP7GocJAxyk").addParams("phone","17666128822").addParams("projectCode","44030120170531001"));
        System.out.println(str);
    }

    public static void main(String[] args) {
        String[] strarr = "NULL,0.01".split(",");
        if(!StringUtils.isEmpty(strarr[0]))
            System.out.println(strarr[0]);
        if(!StringUtils.isEmpty(strarr[1]))
            System.out.println(strarr[1]);
        if(!StringUtils.isEmpty(strarr[2]))
            System.out.println(strarr[2]);
    }
}
