package remote;

import com.pc.wx.po.BaseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Description: ${Description}
 * @Author: 潘锐 (2017-07-19 10:50)
 * @version: \$Rev$
 * @UpdateAuthor: \$Author$
 * @UpdateDateTime: \$Date$
 */
@Controller
public class RemoteAction {
@Autowired
private RemoteService remoteService;
    @RequestMapping(value = "remoteT",method = RequestMethod.GET)
    @ResponseBody
    public BaseResult remoteTest() {
        Map<String, Object> params = new HashMap<>();
        params.put("fdsd", "93fe");
        Map<String, Object> resultMap = null;
        BaseResult result=new BaseResult(0,null);
        try {
            resultMap =  remoteService.remoteTest(params).get(25000l, TimeUnit.MILLISECONDS);
            result.setData(resultMap);
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(1);
            result.setMsg("后台异常");
            return result;
        }
        return result;
    }
}
