package com.hy.wx.service;

import com.alibaba.fastjson.JSON;
import com.hy.wx.http.HttpUtil;
import com.hy.wx.po.ParamsMap;
import com.hy.wx.po.RemoteProtocol;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description: ${Description}
 * @Author: 潘锐 (2017-07-25 18:30)
 * @version: \$Rev$
 * @UpdateAuthor: \$Author$
 * @UpdateDateTime: \$Date$
 */
public class TestService {
    public static void main2(String[] args) {
        String str = HttpUtil.execute(RemoteProtocol.ValidAndUser, ParamsMap.newMap("params", ParamsMap.newMap("openId", "opUEX1V3ssibCUAtfxP7GocJAxyk").addParams("phone", "17666128822").addParams("projectCode", "44030120170531001")));
        System.out.println(str);
    }

    public static void main3(String[] args) {
        String[] strarr = "NULL,0.01".split(",");
        if(!StringUtils.isEmpty(strarr[0]))
            System.out.println(strarr[0]);
        if(!StringUtils.isEmpty(strarr[1]))
            System.out.println(strarr[1]);
        if(!StringUtils.isEmpty(strarr[2]))
            System.out.println(strarr[2]);
    }

    public static void main1(String[] args) {
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
            System.out.println(JSON.toJSONString(menuList));

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

/*    public static void main(String[] args) {
        SimpleTrigger simpleTrigger = (SimpleTrigger) TriggerBuilder.newTrigger().startAt(new Date(System.currentTimeMillis()+10000L)).build();
        try {
            Scheduler scheduler = new StdSchedulerFactory().getScheduler();
            JobDetail jobDetail = JobBuilder.newJob(TestJob.class).build();
//            if (dataMap != null)
//                jobDetail.getJobDataMap().putAll(dataMap);
            scheduler.scheduleJob(jobDetail, simpleTrigger);
            scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }*/
    }
