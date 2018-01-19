package com.hy.wx.po;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * @Description: ${Description}
 * @Author: 潘锐 (2017-07-20 15:33)
 * @version: \$Rev$
 * @UpdateAuthor: \$Author$
 * @UpdateDateTime: \$Date$
 */
@XmlRootElement(name = "msg")
public class Msg implements Serializable {
    private String ToUserName;
/*    private String FromUserName;
    private String CreateTime;
    private String MsgType;
    private String Event;
    private String EventKey;
    private String MenuId;*/
    private String Encrypt;

    public String getToUserName() {
        return ToUserName;
    }

    @XmlElement
    public void setToUserName(String toUserName) {
        ToUserName = toUserName;
    }

/*    public String getFromUserName() {
        return FromUserName;
    }

    public void setFromUserName(String fromUserName) {
        FromUserName = fromUserName;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public String getMsgType() {
        return MsgType;
    }

    public void setMsgType(String msgType) {
        MsgType = msgType;
    }

    public String getEvent() {
        return Event;
    }

    public void setEvent(String event) {
        Event = event;
    }

    public String getEventKey() {
        return EventKey;
    }

    public void setEventKey(String eventKey) {
        EventKey = eventKey;
    }

    public String getMenuId() {
        return MenuId;
    }

    public void setMenuId(String menuId) {
        MenuId = menuId;
    }*/
    @XmlElement
    public String getEncrypt() {
        return Encrypt;
    }

    public void setEncrypt(String encrypt) {
        Encrypt = encrypt;
    }
}
