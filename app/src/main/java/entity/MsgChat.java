package entity;

import java.util.Date;

/**
 * Entity of chat items
 */
public class MsgChat {
    private String content;
    private String  msgDate;
    private String senderName;
    private String imageName;

    public MsgChat() {
    }

    public MsgChat(String content, String msgDate, String senderName) {
        this.content = content;
        this.msgDate = msgDate;
        this.senderName = senderName;
    }

    public MsgChat(String content, String msgDate, String senderName, String imageName) {
        this.content = content;
        this.msgDate = msgDate;
        this.senderName = senderName;
        this.imageName = imageName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMsgDate() {
        return msgDate;
    }

    public void setMsgDate(String msgDate) {
        this.msgDate = msgDate;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
