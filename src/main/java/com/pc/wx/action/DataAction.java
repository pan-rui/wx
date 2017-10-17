package com.pc.wx.action;

import com.pc.wx.base.BaseAction;
import com.pc.wx.po.BaseResult;
import com.pc.wx.po.ParamsMap;
import com.pc.wx.util.ImageCode;
import com.pc.wx.vo.VO;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description: ${Description}
 * @Author: 潘锐 (2017-07-20 10:28)
 * @version: \$Rev$
 * @UpdateAuthor: \$Author$
 * @UpdateDateTime: \$Date$
 */
@RestController
public class DataAction extends BaseAction {

    public ConcurrentHashMap<String,Map> userMap = new ConcurrentHashMap();

    @RequestMapping(value = "/topic/{phone}",method = RequestMethod.GET)
    public BaseResult getIssue(@RequestHeader(required = true) String name, @PathVariable Long phone, @RequestParam Long time) {
        List<Map<String, Object>> topicList = new LinkedList<>();
        topicList.add(ParamsMap.newMap("topic","添加一条用户数据,包含name(String),phone(Long),sex(0 或1)，time(Long)，要求以POST方式请求，JSON格式传递数据(示例：{params:{name:xx,phone:xx,sex:xx,time:xx}})，并将成功后返回的data数据显示到页面上").addParams("url","http://www.l-iot.cn/wx/t1/你电话"));
        topicList.add(ParamsMap.newMap("topic","修改刚才添加的那条数据，以id为条件，将time字段格式化成'yyyy-MM-dd HH:mm:ss'的形式，要求以PUT方式请求，JSON格式传递数据({params:{数据}})，并将成功后返回的data数据显示到页面上").addParams("url","http://www.l-iot.cn/wx/t2/你电话"));
        topicList.add(ParamsMap.newMap("topic","删除刚才添加的那条数据，以id为条件，要求以DELETE方式请求，form表单传递数据，并将成功后返回的data数据显示到页面上").addParams("url","http://www.l-iot.cn/wx/t3/你电话"));
        topicList.add(ParamsMap.newMap("topic","将答案相关代码打包并上传,并带上文件类型（zip,rar)，上传文件字段为file,文件类型字段为fType,要求以POST方式请求，并将成功后返回的l文件url打印到页面上").addParams("url","http://www.l-iot.cn/wx/t4/你电话"));
        return new BaseResult(0, topicList);
    }

    @RequestMapping(value = "t1/{phone}",method = RequestMethod.POST)
    public BaseResult t1(@RequestHeader(required = true)String name, @PathVariable Long phone, @RequestBody VO vo) {
        Map<String,Object> params=vo.getParams();
        if (params.size() < 4) {
            return new BaseResult(1, name+",你的请求缺少参数，格式为：{params:{有效参数}}，请认真审题！");
        }
        params.put("name", name);
        String id=ImageCode.getPartSymbol(8);
        userMap.put(id,params);
        return new BaseResult(0, ParamsMap.newMap("id", id));
}

    @RequestMapping(value = "t2/{phone}",method = RequestMethod.PUT)
    public BaseResult t2(@RequestHeader(required = true) String name, @PathVariable Long phone, @RequestBody VO vo) {
        if (vo.getParams().size() < 2) {
            return new BaseResult(1, name+",你的请求缺少参数，格式为：{params:{有效参数}}，请认真审题！");
        }
        Map<String,Object> uMap=userMap.get(vo.getParams().get("id"));
        uMap.put("time", vo.getParams().get("time"));
        return new BaseResult(0, uMap);
    }

    @RequestMapping(value = "t3/{phone}",method = RequestMethod.DELETE)
    public BaseResult t3(@RequestHeader(required = true) String name, @PathVariable Long phone, @RequestParam(required = true) String id) {
        if (StringUtils.isEmpty(id)) {
            return new BaseResult(1, "id不能为空");
        }
        if (!userMap.containsKey(id)) {
            return new BaseResult(1, "无此id对应的数据");
        }
        return new BaseResult(0, name + ",恭喜您回答正确，还有一道加分题，加油吧！");
    }

    @RequestMapping(value = "t4/{phone}",method = RequestMethod.POST)
    public BaseResult t4(HttpServletRequest request,@RequestHeader(required = true)String name, @PathVariable Long phone,  @RequestParam("file") MultipartFile file, @RequestParam("fType") String fileType) throws IOException {
        if (file == null || StringUtils.isEmpty(fileType)) {
            return new BaseResult(1, "参数有误，请认真审题");
        }
        String fileName = file.getOriginalFilename();
        File newFile = new File(request.getRealPath("/") + fileName);
        file.transferTo(newFile);
        return new BaseResult(0, ParamsMap.newMap("imgUrl", request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() +"/"+ fileName));
    }

}
