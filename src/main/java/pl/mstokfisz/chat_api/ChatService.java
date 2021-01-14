package pl.mstokfisz.chat_api;

import java.util.Date;

public interface ChatService {
    ChatRoom connectUser(String userName, int roomNum, String IP) throws ChatException;
    String disconnectUser(String userName, int roomNum) throws ChatException;
    Object[] getUserList(int roomNum) throws ChatException;
    Object[] getMessageList(int roomNum) throws ChatException;
    String sendMessage(String userName, int roomNum, String message, Date timestamp) throws ChatException;
}
