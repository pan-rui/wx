package com.pc.wx.po;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * @Description: ${Description}
 * @Author: 潘锐 (2017-07-20 15:57)
 * @version: \$Rev$
 * @UpdateAuthor: \$Author$
 * @UpdateDateTime: \$Date$
 */
@XmlRootElement
public class ReMsg implements Serializable {
private String Encrypt;
private String MsgSignature;
private long TimeStamp;
private String Nonce;

    public String getEncrypt() {
        return Encrypt;
    }
@XmlElement
    public void setEncrypt(String encrypt) {
        Encrypt = encrypt;
    }

    public String getMsgSignature() {
        return MsgSignature;
    }
@XmlElement
    public void setMsgSignature(String msgSignature) {
        MsgSignature = msgSignature;
    }

    public long getTimeStamp() {
        return TimeStamp;
    }
@XmlElement
    public void setTimeStamp(long timeStamp) {
        TimeStamp = timeStamp;
    }

    public String getNonce() {
        return Nonce;
    }
@XmlElement
    public void setNonce(String nonce) {
        Nonce = nonce;
    }
}
