package remote;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: ${Description}
 * @Author: 潘锐 (2017-07-17 21:34)
 * @version: \$Rev: 3637 $
 * @UpdateAuthor: \$Author: panrui $
 * @UpdateDateTime: \$Date: 2017-07-18 10:16:22 +0800 (周二, 18 7月 2017) $
 */
@Service
public class LaborQuery{
    @Value("#{config['serverIp']}")
    private String serverIp;
    @Value("#{config['serverPort']}")
    private int serverPort;

    public Map<String, Object> testRemote(HashMap<String, Object> pa) {
        System.out.println(pa);
        HashMap map = new HashMap();
        map.put("t", "panrui");
        return map;
    }

    @PostConstruct
    public void init() {
        Server.serverIp=serverIp;
        Server.serverPort=serverPort;
        Server.run();
    }
}
