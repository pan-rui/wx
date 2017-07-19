package com.pc.wx.service;

import com.pc.wx.http.HttpUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;

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
    @PostConstruct
    public void init() {
        HttpUtil.serverAddr="http://"+serverIp+":"+serverPort;
    }
}
