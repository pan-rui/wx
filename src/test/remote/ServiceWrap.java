package remote;

import com.alibaba.fastjson.JSON;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Administrator on 2015/9/14.
 */
public class ServiceWrap {
//    public static final ServiceType Test_getAdmin = new ServiceType("test", "getAdmin");


    public static Map<String,Object> getServiceResult(Map<String,Object> params) {
        Future<Map<String,Object>> future= Client.getService(params);
        String taskName = future.getClass().getName();
        Map<String,Object> v = null;
        try {
            v = future.get(15000l, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            System.out.println("连接被中断....任务名为：" + JSON.toJSONString(params));
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
/*            if (Server.respGroup.activeCount() < 1)
                System.out.println("服务端未启动．．．．");*/
            System.out.println("获取服务超时....任务名为："+taskName);
            e.printStackTrace();
        }
        return v;
    }
}
