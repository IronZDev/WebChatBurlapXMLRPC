package pl.mstokfisz.chat_server;

import pl.mstokfisz.chat_api.*;

import java.util.ArrayList;
import java.util.Date;

import static java.rmi.server.RemoteServer.getClientHost;

public class ChatServiceImpl implements ChatService {
    private final Chat chat;
    public ChatServiceImpl() {
        chat = new Chat();
    }

    @Override
    public ChatRoom connectUser(String userName, int roomNum, String IP) throws ChatException {
        System.out.println(IP);
        ChatRoom room = chat.getRoom(roomNum);
        room.connectUser(userName, IP);
        return room;
    }

    @Override
    public void disconnectUser(String userName, int roomNum) throws ChatException {
        ChatRoom room = chat.getRoom(roomNum);
        room.disconnectUser(userName);
    }

    @Override
    public ArrayList<User> getUserList(int roomNum) throws ChatException {
        ChatRoom room = chat.getRoom(roomNum);
        return room.getUsersList();
    }

    @Override
    public ArrayList<Message> getMessageList(int roomNum) throws ChatException {
        ChatRoom room = chat.getRoom(roomNum);
        return room.getSentMessages();
    }

    @Override
    public void sendMessage(String userName, int roomNum, String message, Date timestamp) throws ChatException {
        ChatRoom room = chat.getRoom(roomNum);
        room.sendMessage(userName, message, timestamp);
    }
}
