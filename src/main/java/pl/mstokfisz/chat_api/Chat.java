package pl.mstokfisz.chat_api;

import java.util.ArrayList;

public class Chat {
    static private final ArrayList<ChatRoom> chatrooms = new ArrayList<>();
    static {
        for (int chatNum = 1; chatNum <= 3; chatNum++) {
            chatrooms.add(new ChatRoom("Room " + chatNum));
        }
    }

    public ArrayList<ChatRoom> getRoomList() {
        return chatrooms;
    }

    public ChatRoom getRoom(int roomNum) throws ChatException {
        if (chatrooms.get(roomNum) == null) {
            throw new ChatException("There is no room number "+roomNum);
        }
        return chatrooms.get(roomNum);
    }
}
