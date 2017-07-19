package remote;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Description: ${Description}
 * @Author: 潘锐 (2017-07-17 15:39)
 * @version: \$Rev$
 * @UpdateAuthor: \$Author$
 * @UpdateDateTime: \$Date$
 */
public enum RemoteProtocol {
    TEST("laborQuery", "testRemote", new HashMap());
    private String serviceBean;
    private String serviceMethod;
    private Map<String,Object> params=new HashMap<>();

    public String getServiceBean() {
        return serviceBean;
    }

    public String getServiceMethod() {
        return serviceMethod;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    RemoteProtocol(String serviceBean, String serviceMethod, Map<String, Object> params) {
        this.serviceBean=serviceBean;
        this.serviceMethod=serviceMethod;
        this.params=params;
    }

    public Map<String, Object> pack(final Map<String,Object> params) {
        params.put("sBean",serviceBean);
        params.put("sMethod",serviceMethod);
        return params;
    }
}
