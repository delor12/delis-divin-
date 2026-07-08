package com.delisdivin.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable simple memory-based message broker to send messages to clients on prefixes like "/topic" or "/queue"
        config.enableSimpleBroker("/topic", "/queue");
        // Prefix used to filter messages targeted for application annotated methods (e.g. @MessageMapping)
        config.setApplicationDestinationPrefixes("/app");
        // For user-specific messaging (e.g. /user/queue/notifications)
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // End point for WebSocket connection client handshake
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
