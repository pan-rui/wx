package com.pc.wx.action;

import com.alibaba.fastjson.JSON;
import com.pc.wx.aes.AesException;
import com.pc.wx.aes.SHA1;
import com.pc.wx.aes.WxBizMsgCrypt;
import com.pc.wx.base.BaseAction;
import com.pc.wx.http.CacheUtil;
import com.pc.wx.http.HttpUtil;
import com.pc.wx.po.BaseResult;
import com.pc.wx.po.ParamsMap;
import com.pc.wx.po.WxProtocol;
import com.pc.wx.service.RemoteService;
import com.pc.wx.service.WxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: ${Description}
 * @Author: 潘锐 (2017-07-17 15:59)
 * @version: \$Rev$
 * @UpdateAuthor: \$Author$
 * @UpdateDateTime: \$Date$
 */
@Controller
@RequestMapping("verify")
public class VerifyAction extends BaseAction {
    @Autowired
    private WxService wxService;
    @Value("#{config['token']}")
    private String token;
    @Value("#{config['appId']}")
    private String appId;
    @Autowired
    private RemoteService remoteService;
    @Autowired
    private CacheUtil cacheUtil;

    @RequestMapping(value = "", method = RequestMethod.GET)
    protected String verify(final HttpServletRequest request, final HttpServletResponse resp) throws ServletException, IOException {
        String signature = request.getParameter("signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        String echostr = request.getParameter("echostr");
        try {
            if(signature.equals(SHA1.getSHA1(token,timestamp,nonce,""))) {
                returnMsg(resp,echostr);
            }
        } catch (AesException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    @RequestMapping(value = "", method = RequestMethod.POST,params = "openid")
    protected String message(final HttpServletRequest request,HttpServletResponse response, String signature, String timestamp, String nonce, String openid, String encrypt_type, String msg_signature) throws ServletException, IOException {
        String encoding = request.getCharacterEncoding();
        String reMsg=wxService.inMsg(signature, timestamp, nonce, openid, encrypt_type, msg_signature, new String(getRequestPostBytes(request), StringUtils.isEmpty(encoding) ? "UTF-8" : encoding));
        System.out.println("回复消息===>\r\n"+reMsg);
        returnMsg(response,reMsg);
        return null;
    }

    @RequestMapping(value = "auth")
    @ResponseBody
    public Map call(HttpServletRequest request, HttpServletResponse response,String code, final String state) {
        if (StringUtils.isEmpty(code)) return ParamsMap.newMap("301", "后端异常!");
        String[] params = state.split("\\$");
        Map resultMap=wxService.respToken(code,params[0] );
        try {
            if(resultMap.containsKey("data"))
                response.sendRedirect(params[1]);
            else
            response.sendRedirect("/weChat/login.html");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultMap;
    }

    @RequestMapping(value = "token")
    public String getToken(HttpServletResponse response) {
        returnMsg(response, wxService.getToken());
        return null;
    }

/*    @RequestMapping(value = "authToken")
    public String authToken(HttpServletResponse response,@RequestParam(required = false) String openId) {
        returnMsg(response,wxService.reqToken(openId));
        return null;
    }*/

    public void returnMsg(final HttpServletResponse response,String str) {
        try {
            response.getWriter().write(str);
            response.getWriter().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "ticket",method = RequestMethod.GET)
    @ResponseBody
    public Object getParams(HttpServletResponse response) {
        String ticket = cacheUtil.getCache(appId + "_ticket");
        if(StringUtils.isEmpty(ticket)) {
            String jsTicket = HttpUtil.execute(WxProtocol.JSTICKET, ParamsMap.newMap("access_token", wxService.getToken()).addParams("type","jsapi"));
            Map<String, Object> map = JSON.parseObject(jsTicket, Map.class);
            ticket = (String) map.get("ticket");
            if (!StringUtils.isEmpty(ticket))
                cacheUtil.setCacheOnExpire(appId + "_ticket", ticket, Integer.parseInt(map.get("expires_in").toString()) - 30);
        }
        return new BaseResult(0,"OK",ticket);
    }

    @RequestMapping(value = "headInfo",method = RequestMethod.GET)
    @ResponseBody
    public Object getParams(HttpServletResponse response,String nostr) {
        Map<String, Object> headInfo = wxService.getHead(nostr);
        System.out.println("headInfo============>"+headInfo);
        return headInfo;
    }

}
