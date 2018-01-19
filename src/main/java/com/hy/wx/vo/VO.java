package com.hy.wx.vo;


import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Description: ${Description}
 * @Author: 潘锐 (2017-07-25 15:10)
 * @version: \$Rev$
 * @UpdateAuthor: \$Author$
 * @UpdateDateTime: \$Date$
 */
public class VO implements Serializable{
    private Map<String,Object> params = new LinkedHashMap<>();

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
}
