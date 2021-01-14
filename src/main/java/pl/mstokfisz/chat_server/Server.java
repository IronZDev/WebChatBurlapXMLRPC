package pl.mstokfisz.chat_server;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.WebServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.caucho.BurlapServiceExporter;
import org.springframework.remoting.support.RemoteExporter;
import pl.mstokfisz.chat_api.Chat;
import pl.mstokfisz.chat_api.ChatService;

import java.io.IOException;
import java.util.Collections;

@SuppressWarnings("deprecation")
@Configuration @ComponentScan @EnableAutoConfiguration public class Server {
    private final static int xmlPort = 8033;
    private final static String burlapPort = "8032";
    private static final Chat chat = new Chat();

    @Bean
    ChatService chatService() {
        return new ChatServiceImpl();
    }

    @Bean(name = "/chat") RemoteExporter burlapService() {
        BurlapServiceExporter exporter = new BurlapServiceExporter();
        exporter.setService(chatService());
        exporter.setServiceInterface(ChatService.class);
        return exporter;
    }

    public static void main(String[] args) throws IOException, XmlRpcException {
//        XmlRpcHandler xmlRpcHandler = new XmlRpcHandler();
//        xmlRpcHandler.setChat(chat);
        WebServer webServer = new WebServer(xmlPort);
        XmlRpcServer xmlRpcServer = webServer.getXmlRpcServer();
        PropertyHandlerMapping propHandlerMapping = new PropertyHandlerMapping();
        propHandlerMapping.load(Thread.currentThread().getContextClassLoader(), "handler.properties");
        xmlRpcServer.setHandlerMapping(propHandlerMapping);
        XmlRpcServerConfigImpl serverConfig = (XmlRpcServerConfigImpl) xmlRpcServer.getConfig();
        serverConfig.setEnabledForExtensions(true);
        serverConfig.setEnabledForExceptions(true);
        xmlRpcServer.setConfig(serverConfig);
        webServer.start();
        System.out.println("Server XML-RPC started on port: " + xmlPort);

        SpringApplication app = new SpringApplication(Server.class);
        app.setDefaultProperties(Collections.singletonMap("server.port", burlapPort));
        app.run(args);
    }

}