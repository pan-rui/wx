package com.pc.wx.action;

import com.pc.wx.base.BaseAction;
import com.pc.wx.po.BaseResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: ${Description}
 * @Author: 潘锐 (2017-07-20 10:26)
 * @version: \$Rev$
 * @UpdateAuthor: \$Author$
 * @UpdateDateTime: \$Date$
 */
@Controller
public class QueryAction extends BaseAction {

    @RequestMapping(value = "remoteT", method = RequestMethod.GET)
    @ResponseBody
    public BaseResult remoteTest() {
        Map<String, Object> params = new HashMap<>();
        params.put("fdsd", "93fe");
        Map<String, Object> resultMap = null;
        BaseResult result = new BaseResult(0, null);
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
