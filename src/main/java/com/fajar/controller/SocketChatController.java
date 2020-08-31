package com.fajar.controller;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.fajar.dto.ChatMessage;

@Controller
@CrossOrigin
public class SocketChatController {
	Logger log = LoggerFactory.getLogger(SocketChatController.class);
	@Autowired
	private SimpMessagingTemplate webSocket;

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
	public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
		// Add username in web socket session
		headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
		return chatMessage;
	}

//////////////////////////////////////////JAVA DESKTOP CHAT HANDLER //////////////////////////////////////////

	@MessageMapping("/guichat")
	public void chat(Map<String, Object> request, SimpMessageHeaderAccessor headerAccessor) throws IOException {

		log.info("request: {}", request);

		Map<String, Object> sessAttribute = headerAccessor.getSessionAttributes();
		log.info("sessAttribute: {}", sessAttribute);

		String sessionIdFromSession = headerAccessor.getSessionId();
		String subscriptionId = headerAccessor.getSubscriptionId();

		log.info("sessionIdFromSession: {}", sessionIdFromSession);
		log.info("subscriptionId: {}", subscriptionId);
		log.info("headerAccessor session map: {}", headerAccessor.getSessionAttributes().keySet().size());

		webSocket.convertAndSend("/wsResp/chats/" + request.get("messageTo"), request);

	}

}