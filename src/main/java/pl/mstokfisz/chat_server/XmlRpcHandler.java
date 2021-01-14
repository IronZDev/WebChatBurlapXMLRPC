package pl.mstokfisz.chat_server;

import pl.mstokfisz.chat_api.*;

import java.util.ArrayList;
import java.util.Date;

public class XmlRpcHandler {
    private static Chat chat;

    public void setChat (Chat chat) {
        XmlRpcHandler.chat = chat;
    }

    public ChatRoom connectUser(String userName, int roomNum, String IP) throws ChatException {
        System.out.println(userName);
        ChatRoom room = chat.getRoom(roomNum);
        room.connectUser(userName, IP);
        return room;
    }

    public String disconnectUser(String userName, int roomNum) throws ChatException {
        System.out.println(userName);
        return userName;
    }

    public ArrayList<User> getUserList(int roomNum) throws ChatException {
        return null;
    }

    public ArrayList<Message> getMessageList(int roomNum) throws ChatException {
        return null;
    }

    public String sendMessage(String userName, int roomNum, String message, Date timestamp) throws ChatException {
        return null;
    }
}
