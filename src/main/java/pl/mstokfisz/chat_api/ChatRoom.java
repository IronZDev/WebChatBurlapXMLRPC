package pl.mstokfisz.chat_api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class ChatRoom implements Serializable {
    private final String name;

    public ArrayList<Message> getSentMessages() {
        return sentMessages;
    }

    private final ArrayList<Message> sentMessages = new ArrayList<>();
    private final ArrayList<User> userList = new ArrayList<>();

    public ChatRoom(String chatName) {
        this.name = chatName;
    }
    
    private User getUser(String userName) {
        return userList.stream().filter(o -> o.getName().equals(userName)).findAny().orElse(null);
    }

    public void connectUser(String userName, String IP) throws ChatException {
        User user = getUser(userName);
        System.out.println(user);
        if (user != null) {
            if (user.isOnline()) {
                System.out.println("User already connected!");// User already connected
                throw new ChatException("User with this username is already connected!");
            } else {
                if (user.getIP().equals(IP)) {
                    user.setOnline();
                } else {
                    System.out.println("Username is already taken!");// User already connected
                    throw new ChatException("Username is already taken!");
                }
            }
        } else {
            userList.add(new User(userName, IP));
        }
    }

    public void disconnectUser(String userName) throws ChatException {
        User user = getUser(userName);
        if (user == null) {
            System.out.println("User with that username is not connected!");// User already connected
            throw new ChatException("User with that username is not connected!");
        } else {
            getUser(userName).setOffline();
        }
    }

    public ArrayList<User> getUsersList() {
        return userList;
    }

    public void sendMessage(String userName, String message, Date timestamp) throws ChatException {
        User user = getUser(userName);
        if (user == null) {
            System.out.println("User with that username is not connected!");// User already connected
            throw new ChatException("User with that username is not connected!");
        }
        sentMessages.add(new Message(user, message, timestamp));
    }
}
