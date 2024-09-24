package fr.utc.multeract.server.configs;

import fr.utc.multeract.server.endpoints.WebsocketHistoryMessageHandler;
import fr.utc.multeract.server.middleware.EnsureHistoryTokenValidHandshakeInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@Configuration
@EnableWebSocket
public class WebsocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(userMessageHandler(), "/instance/ws").setAllowedOrigins("*").addInterceptors(getInter());
    }

    @Bean
    public WebSocketHandler userMessageHandler() {
        return new WebsocketHistoryMessageHandler();
    }

    @Bean
    public HandshakeInterceptor getInter() {
        return new EnsureHistoryTokenValidHandshakeInterceptor();
    }

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(8192);
        container.setMaxBinaryMessageBufferSize(8192);
        return container;
    }
}
