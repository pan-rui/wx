package com.pc.wx.action;

import com.alibaba.fastjson.JSON;
import com.pc.wx.base.BaseAction;
import com.pc.wx.http.CacheUtil;
import com.pc.wx.http.HttpUtil;
import com.pc.wx.po.BaseResult;
import com.pc.wx.po.ParamsMap;
import com.pc.wx.service.RemoteService;
import com.pc.wx.service.WxService;
import com.pc.wx.vo.VO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.WebAsyncTask;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Description: ${Description}
 * @Author: 潘锐 (2017-07-20 10:28)
 * @version: \$Rev$
 * @UpdateAuthor: \$Author$
 * @UpdateDateTime: \$Date$
 */
@Controller
public class WxAction extends BaseAction {
    @Autowired
    private WxService wxService;
    @Autowired
    private RemoteService remoteService;
    @Autowired
    private CacheUtil cacheUtil;

    @RequestMapping("createMenu")
    @ResponseBody
    public Map createMenu(@RequestParam(required = true) String aToken) {
        List<Map> menuList = new ArrayList<>();
        try {
            menuList.add(ParamsMap.newMap("name", new String("查询信息".getBytes(), "UTF-8")).addParams("sub_button", Arrays.asList(
                    ParamsMap.newMap("type", "view").addParams("name", new String("工资查询".getBytes(), "UTF-8")).addParams("url", "http://www.qugongdi.com/weChat/view/labour/salary.html"),
                    ParamsMap.newMap("type", "view").addParams("name", new String("考勤查询".getBytes(), "UTF-8")).addParams("url", "http://www.qugongdi.com/weChat/view/labour/clocking-in.html")
            )));
            menuList.add(ParamsMap.newMap("name", new String("关于我们".getBytes(), "UTF-8")).addParams("sub_button", Arrays.asList(
                    ParamsMap.newMap("type", "view").addParams("name", new String("产品简介".getBytes(), "UTF-8")).addParams("url", "http://www.qugongdi.com/weChat/login.html"),
                    ParamsMap.newMap("type", "view").addParams("name", new String("推广活动".getBytes(), "UTF-8")).addParams("url", "http://www.qugongdi.com/weChat/login.html"),
                    ParamsMap.newMap("type", "view").addParams("name", new String("关于靠得筑".getBytes(), "UTF-8")).addParams("url", "http://www.qugongdi.com/weChat/login.html")
            )));
            menuList.add(ParamsMap.newMap("name", new String("个人信息".getBytes(), "UTF-8")).addParams("sub_button", Arrays.asList(
                    ParamsMap.newMap("type", "view").addParams("name", new String("绑定项目".getBytes(), "UTF-8")).addParams("url", "http://www.qugongdi.com/weChat/login.html"),
                    ParamsMap.newMap("type", "view").addParams("name", new String("信息录入".getBytes(), "UTF-8")).addParams("url", "http://www.qugongdi.com/weChat/view/labour/user-message.html"),
                    ParamsMap.newMap("type", "view").addParams("name", new String("个人信息".getBytes(), "UTF-8")).addParams("url", "http://www.qugongdi.com/weChat/login.html")
            )));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return wxService.createMenu(aToken, menuList);
    }

    @RequestMapping(value = "userInfo", method = RequestMethod.GET)
    @ResponseBody
    public Map getUserInfo(String state, String openId, String tenantId, String ddBB) {
        Map<String, Object> userMap = null;
        if (StringUtils.isEmpty(openId))
            openId = cacheUtil.getCache(state);
        String userStr = cacheUtil.hGetCache("USERINFO", openId);
        if (StringUtils.isEmpty(userStr))
            userMap = remoteService.getUserInfo(ddBB, openId, tenantId);
        else {
            userMap = JSON.parseObject(userStr, Map.class);
        }
        return userMap;
    }

    @RequestMapping(value = "userInfo", method = RequestMethod.POST)
    @ResponseBody
    public Map addUserInfo(@RequestBody VO vo) {
        Map<String,Object> params=vo.getParams();
        System.out.println(params);
        return remoteService.addUser(params.get("openId").toString(), params.get("tenantId").toString(), params.get("projectCode").toString(), params.get("ddBB").toString(), (Map)params.get("userInfo"));
    }

    @RequestMapping(value = "sendSMS", method = RequestMethod.GET)
    @ResponseBody
    public WebAsyncTask<String> sendSMS(HttpSession session, String phone, String type) {
        return new WebAsyncTask<String>(3500l, () -> {
            String smsCode = "12345678";
            System.out.println("获取短信..........============"+type);
            cacheUtil.setCacheOnExpire("phone_" + type, smsCode, 1800);
            return "12345678";
        });
    }

    @RequestMapping(value = "bind", method = RequestMethod.POST)
    @ResponseBody
    public BaseResult bind(String phone, String pCode, String type, String phoneCode, String openId) {     //pCode:项目编号
        String smsCode = cacheUtil.getCache("phone_" + type);
        if (StringUtils.isEmpty(smsCode)) {
            return new BaseResult(102,"验证码已过期!");
        }else if(smsCode.equals(phoneCode)){
            //远程验证是否存在
            Map<String,Object> resultMap= remoteService.validAndUser(phone, pCode, openId,wxService.getWxUser(openId));
//            resultMap.get()
//            return ParamsMap.newMap("code", 0).addParams("msg", "OK");
                return new BaseResult((Integer) resultMap.get("code"),wxService.getHeadForOid(openId));
        }else {
            return new BaseResult(101,"验证码错误!");
        }
    }

/*    @RequestMapping(value = "imgCode", method = RequestMethod.GET)
    @ResponseBody
    public Object imageCode(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        ImageCode.generatorImg(request, response);
        return null;
    }*/

    @RequestMapping(value = "extInfo",method = RequestMethod.GET)
    @ResponseBody
    public Object extInfo(HttpServletRequest request, String openId) {
        return remoteService.extInfo(openId);
    }

    public Map getCardImg(HttpServletRequest request, String openId, String media) {
        System.out.println("openId=====>"+openId+"          =========media"+media);
        return wxService.downImg(openId, media);
    }

    @RequestMapping(value = "checkWork", method = RequestMethod.GET)
    @ResponseBody
    public Object getCheckWork(HttpServletRequest request, String tenantId, String ddBB, @RequestParam(required = true) String openId, String projectCode, String month) {
        return remoteService.getCheckWork(openId, tenantId, ddBB, projectCode, month);
    }
}
