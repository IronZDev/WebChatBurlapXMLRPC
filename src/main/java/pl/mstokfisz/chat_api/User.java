package pl.mstokfisz.chat_api;

import java.io.Serializable;

public class User implements Serializable {
    private final String IP;
    private final String name;
    private boolean online;

    public User(String name, String IP) {
        this.IP = IP;
        this.name = name;
        online = true;
    }

    public String getName() {
        return name;
    }

    public String getIP() {
        return IP;
    }

    public void setOnline() {
        online = true;
    }

    public void setOffline() {
        online = false;
    }

    public boolean isOnline() {
        return online;
    }
}
