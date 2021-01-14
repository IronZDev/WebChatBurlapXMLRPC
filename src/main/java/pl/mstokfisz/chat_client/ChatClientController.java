package pl.mstokfisz.chat_client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.WindowEvent;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientException;
import pl.mstokfisz.chat_api.*;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ChatClientController {
    @FXML
    private Button connectBtn;
    @FXML
    private TextField usernameTextField;
    @FXML
    private ChoiceBox chooseRoomCB;
    @FXML
    private TextArea connectedUsersTextView;
    @FXML
    private TextArea messagesTextView;
    @FXML
    private Button sendBtn;
    @FXML
    private TextField sendTextFlield;

    private ChatService service;
    private XmlRpcClient xmlRpcClient;
    private boolean usernameValid = false;
    private boolean roomValid = false;
    private boolean connected = false;
    private UpdaterThread updaterThread;

    Alert a = new Alert(Alert.AlertType.ERROR);

    public void setService(ChatService service, XmlRpcClient xmlRpcClient) {
        this.service = service;
        this.xmlRpcClient = xmlRpcClient;
    }

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            System.out.println(service);
            System.out.println("Controller started!");
        });
    }

    @FXML
    private void validateUsername(Event e) {
        usernameValid = ((TextField)e.getTarget()).getText().length() > 0;
        checkValidity();
    }

    @FXML
    private void validateRoom(Event e) {
        roomValid = ((ChoiceBox)e.getTarget()).getValue().toString().length() > 0;
        checkValidity();
    }

    @FXML
    private void validateMessage(Event e) {
        sendBtn.setDisable(((TextField)e.getTarget()).getText().length() == 0);
    }

    @FXML
    private void sendMessage(Event e) {
        try {
            Object[] params = new Object[] {usernameTextField.getText(), chooseRoomCB.getSelectionModel().getSelectedIndex(), sendTextFlield.getText(), new Date()};
            String response = (String) xmlRpcClient.execute("ChatService.sendMessage", params);
            System.out.println("Message : " + response);
//            service.sendMessage(usernameTextField.getText(), chooseRoomCB.getSelectionModel().getSelectedIndex(), sendTextFlield.getText(), new Date());
            sendTextFlield.setText("");
        } catch (Exception ex) {
            a.setTitle("Could not send message!");
            a.setContentText(ex.getMessage());
            a.show();
        }
    }

    @FXML
    private void connectToRoom(Event e) {
        if (!connected) {
            try {
                String IP = InetAddress.getLocalHost().getHostAddress();
                Object[] params = new Object[] {usernameTextField.getText(), chooseRoomCB.getSelectionModel().getSelectedIndex(), IP};
                ChatRoom chatRoom = (ChatRoom) xmlRpcClient.execute("ChatService.connectUser", params);
//                ChatRoom chatRoom = service.connectUser(usernameTextField.getText(), chooseRoomCB.getSelectionModel().getSelectedIndex(), IP);
                System.out.println(chatRoom.getUsersList());
                usernameTextField.setDisable(true);
                chooseRoomCB.setDisable(true);
                connectBtn.setText("Disconnect!");
                connected = true;
                updaterThread = new UpdaterThread(xmlRpcClient);
                updaterThread.start();
                checkValidity();
            } catch (XmlRpcClientException chatException) {
                usernameTextField.setDisable(false);
                chooseRoomCB.setDisable(false);
                connectBtn.setText("Connect!");
                connected = false;
                checkValidity();
                cleanUp();
                a.setTitle("Could not connect to the room!");
                a.setContentText(chatException.getMessage());
                a.show();
            } catch (Exception ex) {
                a.setContentText(ex.getMessage());
                a.show();
            }
        } else {
            try {
                updaterThread.stop();
                Object[] params = new Object[] {usernameTextField.getText(), chooseRoomCB.getSelectionModel().getSelectedIndex()};
                String response = (String) xmlRpcClient.execute("ChatService.disconnectUser", params);
//                service.disconnectUser(usernameTextField.getText(), chooseRoomCB.getSelectionModel().getSelectedIndex());
                usernameTextField.setDisable(false);
                chooseRoomCB.setDisable(false);
                connectBtn.setDisable(false);
                connectBtn.setText("Connect!");
                connected = false;
                cleanUp();
                checkValidity();
            } catch (Exception ex) {
                cleanUp();
                a.setTitle("Could not disconnect from the room!");
                a.setContentText(ex.getMessage());
                a.show();
            }
        }
    }

    public void exitApplication(Event e) {
        if (connected) {
            System.out.println("Disconnect!");
            connected = false;
            try {
                service.disconnectUser(usernameTextField.getText(), chooseRoomCB.getSelectionModel().getSelectedIndex());
            } catch (Exception ex) {
                System.out.println("Error disconnecting from server: "+ex.getMessage());
            }
        }
        Platform.exit();
    }

    private void checkValidity() {
        connectBtn.setDisable(!usernameValid || !roomValid);
        sendTextFlield.setDisable(!connected);
        if (!connected) {
            sendBtn.setDisable(true);
        }
    }

    private void cleanUp() {
        messagesTextView.setText("");
        connectedUsersTextView.setText("");
        sendTextFlield.setText("");
    }

    private void updateUserList(ArrayList<User> userList) {
        System.out.println("Update users");
        List<String> names = userList.stream().map(o -> o.getName() + " (" + (o.isOnline() ? "Online" : "Offline") + ")").collect(Collectors.toList());
        connectedUsersTextView.setText(String.join("\n", names));
    }

    private void updateMessageList(ArrayList<Message> messageList) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        System.out.println("Update messages");
        List<String> messageStrings = messageList.stream().map(o -> o.getSender().getName() + "(" + format.format(o.getTimestamp()) + "): " + o.getText()).collect(Collectors.toList());
        messagesTextView.setText(String.join("\n", messageStrings));
    }

    public class UpdaterThread extends Thread {
        private XmlRpcClient xmlRpcClientThread;
        private ArrayList<User> lastUserList = null;
        private ArrayList<Message> lastMessageList = null;

        public UpdaterThread(XmlRpcClient xmlRpcClient) {
            this.xmlRpcClientThread = xmlRpcClient;
        }

        private boolean compareUserLists(ArrayList<User> list) {
            if (lastUserList.size() != list.size()) {
                return false;
            }
            for (User user: lastUserList) {
                if (list.stream().noneMatch(o -> o.getName().equals(user.getName()))) {
                    return false;
                } else if (list.stream().filter(o -> o.getName().equals(user.getName())).findAny().get().isOnline() != user.isOnline()) {
                   return false;
                }
            }
            return true;
        }

        private boolean compareMessageLists(ArrayList<Message> list) {
            return lastMessageList.size() == list.size();
        }

        public void run(){
            while(connected) {
                try {
                    Object[] params = new Object[] {chooseRoomCB.getSelectionModel().getSelectedIndex()};
                    ArrayList<User> userList = new ArrayList<>((List<User>) (Object)Arrays.asList((Object[]) xmlRpcClientThread.execute("ChatService.getUserList", params)));
                    ArrayList<Message> messageList = new ArrayList<>((List<Message>) (Object)Arrays.asList((Object[]) xmlRpcClientThread.execute("ChatService.getMessageList", params)));
//                    ArrayList<User> userList = (User[]) xmlRpcClientThread.execute("ChatService.getUserList", params);
//                    ArrayList<Message> messageList = (Message[]) xmlRpcClientThread.execute("ChatService.getMessageList", params);
//                    ArrayList<User> userList = service.getUserList(chooseRoomCB.getSelectionModel().getSelectedIndex());
//                    ArrayList<Message> messageList = service.getMessageList(chooseRoomCB.getSelectionModel().getSelectedIndex());
                    if (lastUserList == null || !compareUserLists(userList)) {
                        updateUserList(userList);
                        lastUserList = userList;
                    }
                    if (lastMessageList == null || !compareMessageLists(messageList)) {
                        updateMessageList(messageList);
                        lastMessageList = messageList;
                    }
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (Exception e) {
                    System.out.println("Problem");
                    e.printStackTrace();
                    Platform.runLater(new Runnable() {
                        @Override public void run() {
                            a.setTitle("Could not download data from the server");
                            a.setContentText(e.getMessage());
                            a.show();
                        }
                    });

                }
            }
        }
    }
}
