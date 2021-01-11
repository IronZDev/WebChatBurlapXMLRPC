package pl.mstokfisz.chat_api;

import java.util.ArrayList;
import java.util.Date;

public interface ChatService {
    ChatRoom connectUser(String userName, int roomNum, String IP) throws ChatException;
    void disconnectUser(String userName, int roomNum) throws ChatException;
    ArrayList<User> getUserList(int roomNum) throws ChatException;
    ArrayList<Message> getMessageList(int roomNum) throws ChatException;
    void sendMessage(String userName, int roomNum, String message, Date timestamp) throws ChatException;
}
