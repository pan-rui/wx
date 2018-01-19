package com.hy.wx.action;

import com.alibaba.fastjson.JSON;
import com.hy.wx.http.CacheUtil;
import com.hy.wx.http.HttpUtil;
import com.hy.wx.po.CacheKey;
import com.hy.wx.po.WxProtocol;
import com.hy.wx.vo.VO;
import com.hy.wx.base.BaseAction;
import com.hy.wx.po.BaseResult;
import com.hy.wx.po.ParamsMap;
import com.hy.wx.service.RemoteService;
import com.hy.wx.service.WxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.WebAsyncTask;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
    private DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
    public Map getUserInfo(String state, @RequestParam(required = true) String openId, String tenantId, String ddBB) {
        Map<String, Object> userMap = null;
        if (StringUtils.isEmpty(openId))
            openId = cacheUtil.getCache(state);
        if (StringUtils.isEmpty(openId)) {
            return ParamsMap.newMap("code", 10).addParams("msg", "openId为空,请重新授权!");
        }
//        String userStr = cacheUtil.hGetCache("USERINFO", openId);
//        if (StringUtils.isEmpty(userStr))
            userMap = remoteService.getUserInfo(ddBB, openId, tenantId);
/*        else {
            userMap =ParamsMap.newMap("code",0).addParams("data",JSON.parseObject(userStr, Map.class));
        }*/
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
        return new WebAsyncTask<String>(3500L, () -> {
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
            Map<String,Object> wxUser=wxService.getWxUser(openId);
            if(CollectionUtils.isEmpty(wxUser)) return new BaseResult(80, "请重新进行授权!");
            Map<String,Object> resultMap= remoteService.validAndUser(phone, pCode, openId,wxUser);
//            resultMap.get()
//            return ParamsMap.newMap("code", 0).addParams("msg", "OK");
                return new BaseResult((Integer) resultMap.get("code"),wxService.getHeadForOid(openId));
        }else {
            return new BaseResult(101,"验证码错误!");
        }
    }

    @RequestMapping(value = "extInfo",method = RequestMethod.GET)
    @ResponseBody
    public Object extInfo(HttpServletRequest request, @RequestParam(required = true) String openId) {
        return remoteService.extInfo(openId);
    }

    @RequestMapping(value = "checkWork", method = RequestMethod.GET)
    @ResponseBody
    public Object getCheckWork(HttpServletRequest request, String tenantId, String ddBB, @RequestParam(required = true) String openId, String projectCode, String month) {
        return remoteService.getCheckWork(openId, tenantId, ddBB, projectCode, month);
    }
    @RequestMapping(value = "salary", method = RequestMethod.GET)
    @ResponseBody
    public Object getSalary(HttpServletRequest request, String tenantId, String ddBB, @RequestParam(required = true) String openId, String projectCode) {
        return remoteService.getSalary(openId, tenantId, ddBB, projectCode);
    }

    @RequestMapping(value = "mediaId",method = RequestMethod.GET)
    @ResponseBody
    public Object getServerId(HttpServletRequest request, @RequestParam(required = true) String openId, String tenantId, String projectCode, String ddBB, @RequestParam(required = true) String serverId,@RequestParam(required = true) int isFront) {
        return remoteService.downImg(openId, tenantId, ddBB, projectCode, serverId, isFront, wxService.getToken());
    }

    @RequestMapping(value = "paySuccessMsg",method = RequestMethod.POST)
    @ResponseBody
    public BaseResult sendPaySuccessMsg(HttpServletRequest request, @RequestBody VO vo) {
        BaseResult baseResult = new BaseResult(0, "OK");
        Map<String, Object> params = vo.getParams();
        Map<String,Object> userMap= (Map<String, Object>) params.get("user");
        Map<String,Object> orderMap= (Map<String, Object>) params.get("order");
        String sex = (userMap.get("cardNo").toString().charAt(16) & 1) == 1 ? "先生" : "女士";
        Map<String, Object> resultMap = JSON.parseObject(HttpUtil.execute(WxProtocol.SEND_TEMPLATE_MSG, ParamsMap.newMap("access_token", wxService.getToken())
                .addParams("touser", userMap.get("openId"))
                .addParams("template_id", CacheKey.PAY_SUCCESS_MSG_ID)
                .addParams("url", "http://wx.shenzhenhengyong.com/wx/?TypeUrl=order/0")
                .addParams("data", ParamsMap.newMap("first", ParamsMap.newMap("value", userMap.get("name") + sex + ",我们已收到您的货款,开始为您打包商品,请耐心等待:"))
                        .addParams("orderMoneySum", ParamsMap.newMap("value", params.get("amount")).addParams("color", "#ff0000"))
                        .addParams("orderProductName", ParamsMap.newMap("value", orderMap.get("productName")))
                        .addParams("Remark", ParamsMap.newMap("value", "您的订单号为:" + orderMap.get("orderNo") + ",如有疑问请至电400-889-3211或直接在微信留言,我们将第一时间为您服务.")))), Map.class);
        if (!"0".equals(resultMap.get("errcode").toString())) {
            baseResult.setCode(108);
            baseResult.setMsg(resultMap.get("errmsg").toString());
            baseResult.setData(resultMap.get("msgid"));
        }
        return baseResult;
    }

    @RequestMapping(value = "payNotifyMsg",method = RequestMethod.POST)
    @ResponseBody
    public BaseResult sendPayNotifyMsg(HttpServletRequest request, @RequestBody VO vo) {
        BaseResult baseResult = new BaseResult(0, "OK");
        Map<String, Object> params = vo.getParams();
        Map<String,Object> userMap= (Map<String, Object>) params.get("user");
        Map<String,Object> orderMap= (Map<String, Object>) params.get("order");
        String sex = (userMap.get("cardNo").toString().charAt(16) & 1) == 1 ? "先生" : "女士";
        Map<String, Object> resultMap = JSON.parseObject(HttpUtil.execute(WxProtocol.SEND_TEMPLATE_MSG, ParamsMap.newMap("access_token", wxService.getToken())
                .addParams("touser", userMap.get("openId"))
                .addParams("template_id", CacheKey.PAY_NOTIFY_MSG_ID)
                .addParams("url", "http://wx.shenzhenhengyong.com/wx/?TypeUrl=order/0")
                .addParams("data", ParamsMap.newMap("first", ParamsMap.newMap("value", userMap.get("name") + sex + ",您好!您的订单尚未支付.")).
                        addParams("ordertape", ParamsMap.newMap("value", sdf.format(orderMap.get("ctime").toString())))
                        .addParams("orderID", ParamsMap.newMap("value", orderMap.get("orderNo")).addParams("color","#ff0000"))
                        .addParams("remark", ParamsMap.newMap("value", "待支付金额为:"+orderMap.get("payMoney")+"元,未付款订单将在24小时内关闭,请及时付款.恒雍金服竭诚为您服务,点击查看详情.")))), Map.class);
        if (!"0".equals(resultMap.get("errcode").toString())) {
            baseResult.setCode(108);
            baseResult.setMsg(resultMap.get("errmsg").toString());
            baseResult.setData(resultMap.get("msgid"));
        }
        return baseResult;
    }

    @GetMapping("quit")
    @ResponseBody
    public BaseResult quit(@RequestHeader(CacheKey.OPENID) String openId,@RequestHeader(CacheKey.USER_ID)Object uId) {
        cacheUtil.hDelCache(CacheKey.WX_HEAD, CacheKey.USER_ID+openId);
        cacheUtil.hDelCache(CacheKey.WX_HEAD, CacheKey.OPENID+openId);
        cacheUtil.hDelCache(CacheKey.WX_HEAD, CacheKey.USER_PHONE+openId);
        return new BaseResult(0, "OK");
    }
}
