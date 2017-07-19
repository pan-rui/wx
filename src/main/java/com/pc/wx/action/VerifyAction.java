package com.pc.wx.action;

import com.pc.wx.base.BaseAction;
import com.pc.wx.po.BaseResult;
import com.pc.wx.po.RemoteProtocol;
import com.pc.wx.service.RemoteService;
import com.pc.wx.service.WxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Description: ${Description}
 * @Author: 潘锐 (2017-07-17 15:59)
 * @version: \$Rev$
 * @UpdateAuthor: \$Author$
 * @UpdateDateTime: \$Date$
 */
@Controller
@RequestMapping("verify")
public class VerifyAction extends BaseAction{
    @Autowired
    private WxService wxService;
    @Autowired
    private RemoteService remoteService;

    /*    protected void doGet(final HttpServletRequest request, final HttpServletResponse resp) throws ServletException, IOException {
            System.out.println(msg);
            this.timestamp = Long.toString(System.currentTimeMillis());
            String signature = request.getParameter("signature");
            String timestamp = request.getParameter("timestamp");
            String nonce = request.getParameter("nonce");
            String echostr = request.getParameter("echostr");
            System.out.println("timestamp===========>" + timestamp);
            System.out.println("nonce===========>" + nonce);
            System.out.println("echostr===========>" + echostr);
            System.out.println("signature===========>" + signature);
            try {
    //            String signature2 = getSHA1(token, timestamp, nonce, EncodingAESKey);
                String signature2 = getSHA1(token, timestamp, nonce, EncodingAESKey);
                System.out.println(signature2);
                if (signature2.equals(signature)) {
                    resp.getWriter().write(echostr);
                    resp.getWriter().close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
    @RequestMapping(value = "")
    @ResponseBody
    public String call(String code,String state) {
        if(StringUtils.isEmpty(code)) return "FAIL";
        return wxService.respToken(code, state);
    }

    @RequestMapping(value = "token", method = RequestMethod.GET)
    @ResponseBody
    public String getToken() {
        return wxService.getToken();
    }

    @RequestMapping(value = "authToken",method = RequestMethod.GET)
    public String authToken(@RequestParam(required = false) String openId) {
        return wxService.reqToken(openId);
    }

    @RequestMapping(value = "remoteT",method = RequestMethod.GET)
    @ResponseBody
    public BaseResult remoteTest() {
        Map<String, Object> params = new HashMap<>();
        params.put("fdsd", "93fe");
        Map<String, Object> resultMap = null;
        BaseResult result=new BaseResult(0,null);
        try {

        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(1);
            result.setMsg("后台异常");
            return result;
        }
        return result;
    }
}
