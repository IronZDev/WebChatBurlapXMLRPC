package pl.mstokfisz.chat_client;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.caucho.BurlapProxyFactoryBean;
import org.springframework.util.SocketUtils;
import pl.mstokfisz.chat_api.ChatException;
import pl.mstokfisz.chat_api.ChatRoom;
import pl.mstokfisz.chat_api.ChatService;
import javafx.application.Application;

import java.io.File;
import java.net.InetAddress;
import java.net.URL;

import static java.lang.System.out;

@ComponentScan
@EnableAutoConfiguration
@Configuration
@SuppressWarnings("deprecation")
public class BurlapClient extends Application implements EmbeddedServletContainerCustomizer {
    private static ChatService service;
    private static XmlRpcClient xmlRpcClient;


    @Bean(name = "bulrapInvoker")
    public BurlapProxyFactoryBean burlapInvoker() {
        BurlapProxyFactoryBean invoker = new BurlapProxyFactoryBean();
        invoker.setServiceUrl("http://localhost:8032/chat");
        invoker.setServiceInterface(ChatService.class);
        return invoker;
    }


    public static void main(String[] args) {
        service = SpringApplication.run(BurlapClient.class, args).getBean(ChatService.class);
        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL("http://localhost:8033/xmlrpc/chat"));
            config.setEnabledForExtensions(true);
            xmlRpcClient = new XmlRpcClient();
            xmlRpcClient.setConfig(config);
            Object[] params = new Object[] {"Maciek", 0, InetAddress.getLocalHost().getHostAddress()};
            ChatRoom response = (ChatRoom) xmlRpcClient.execute("XmlRpcHandler.connectUser", params);
            System.out.println("Message : " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        launch(args);
    }

    @Override
    public void customize(ConfigurableEmbeddedServletContainer configurableEmbeddedServletContainer) {
        int freePort = SocketUtils.findAvailableTcpPort();
        configurableEmbeddedServletContainer.setPort(freePort);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL url = new File("src/main/java/pl/mstokfisz/chat_client/ChatClientUI.fxml").toURI().toURL();
        FXMLLoader fxmlLoader = new FXMLLoader(url);
        Parent root = fxmlLoader.load();

        ChatClientController controller = fxmlLoader.getController();
        controller.setService(service);

        Scene scene = new Scene(root, 1240, 720);

        primaryStage.setTitle("Webchat");
        primaryStage.setOnHidden(controller::exitApplication);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop(){
        System.exit(1);
    }
}
