package com.fajar.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.fajar.dto.ChatMessage;

@Controller
public class SocketChatController {
	Logger log = LoggerFactory.getLogger(SocketChatController.class);
	
	
	public SocketChatController() {
		log.info("------------------SOCKET CHAT CONTROLLER #1-----------------");
	}

    @MessageMapping("/chat.sendMessage")
    @SendTo("/wsResp/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/wsResp/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, 
                               SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }

}