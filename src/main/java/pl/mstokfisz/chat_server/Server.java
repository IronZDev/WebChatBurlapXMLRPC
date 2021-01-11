package pl.mstokfisz.chat_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.caucho.BurlapServiceExporter;
import org.springframework.remoting.support.RemoteExporter;
import pl.mstokfisz.chat_api.ChatService;

import java.util.Collections;

@SuppressWarnings("deprecation")
@Configuration @ComponentScan @EnableAutoConfiguration public class Server {

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

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Server.class);
        app.setDefaultProperties(Collections.singletonMap("server.port", "8032"));
        app.run(args);
    }

}