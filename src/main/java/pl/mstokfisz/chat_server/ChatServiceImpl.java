package pl.mstokfisz.chat_server;

import pl.mstokfisz.chat_api.*;

import java.util.Date;


public class ChatServiceImpl implements ChatService {
    private static final Chat chat = new Chat();

    @Override
    public ChatRoom connectUser(String userName, int roomNum, String IP) throws ChatException {
        System.out.println(IP);
        ChatRoom room = chat.getRoom(roomNum);
        room.connectUser(userName, IP);
        return room;
    }

    @Override
    public String disconnectUser(String userName, int roomNum) throws ChatException {
        ChatRoom room = chat.getRoom(roomNum);
        room.disconnectUser(userName);
        return userName;
    }

    @Override
    public Object[] getUserList(int roomNum) throws ChatException {
        ChatRoom room = chat.getRoom(roomNum);
        return room.getUsersList().toArray();
    }

    @Override
    public Object[] getMessageList(int roomNum) throws ChatException {
        ChatRoom room = chat.getRoom(roomNum);
        return room.getSentMessages().toArray();
    }

    @Override
    public String sendMessage(String userName, int roomNum, String message, Date timestamp) throws ChatException {
        ChatRoom room = chat.getRoom(roomNum);
        room.sendMessage(userName, message, timestamp);
        return message;
    }
}
