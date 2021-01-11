package pl.mstokfisz.chat_api;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {
    private User sender;
    private String text;
    private Date timestamp;

    public Message(User sender, String text, Date timestamp) {
        this.sender = sender;
        this.text = text;
        this.timestamp = timestamp;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}


