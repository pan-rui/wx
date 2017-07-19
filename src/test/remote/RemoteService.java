package remote;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * @Description: ${Description}
 * @Author: 潘锐 (2017-07-17 17:14)
 * @version: \$Rev$
 * @UpdateAuthor: \$Author$
 * @UpdateDateTime: \$Date$
 */
@Service
//@Async
public class RemoteService {

    @Value("#{config['serverIp']}")
    private String serverIp;
    @Value("#{config['serverPort']}")
    private int serverPort;
    public ListenableFuture<Map> remoteTest(Map<String,Object> pa) {
        return new AsyncResult(ServiceWrap.getServiceResult(RemoteProtocol.TEST.pack(pa)));
    }

    @PostConstruct
    public void init() {
        Client.serverIp=serverIp;
        Client.serverPort=serverPort;
    }
}
