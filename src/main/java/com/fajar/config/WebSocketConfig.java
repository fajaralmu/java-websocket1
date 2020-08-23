package com.fajar.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableScheduling
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
 
	Logger log = LoggerFactory.getLogger(WebSocketConfig.class);
	public WebSocketConfig() {
		log.info("WebSocketConfig");
	}
	
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
    	log.info("configureMessageBroker");
      //  config.enableSimpleBroker("/topic");
        config.enableSimpleBroker("/wsResp");
        config.setApplicationDestinationPrefixes("/app");
    }
 
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
    	log.info("registerStompEndpoints");
      //   registry.addEndpoint("/chat");
         registry.addEndpoint("/random").setAllowedOrigins("*").withSockJS();
         registry.addEndpoint("/ws").setAllowedOrigins("*").withSockJS();
         registry.addEndpoint("/game-app").setAllowedOrigins("*").withSockJS();
    }
}