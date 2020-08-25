package com.fajar.controller;

import static com.fajar.parameter.RestParameter.APPLICATION_JSON;
import static com.fajar.parameter.RestParameter.UTF_8;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fajar.dto.Message;
import com.fajar.dto.OutputMessage;
import com.fajar.dto.RealtimeRequest;
import com.fajar.dto.RealtimeResponse;
import com.fajar.service.GameSettingService;
import com.fajar.service.RealtimeService;

@CrossOrigin
@RestController
public class SocketController {
	Logger log = LoggerFactory.getLogger(SocketController.class);
	@Autowired
	private SimpMessagingTemplate webSocket;
	@Autowired
	private RealtimeService realtimeUserService;
	@Autowired
	private GameSettingService gameSettingService;

	public SocketController() {
		log.info("------------------SOCKET CONTROLLER #1-----------------");
	}

	@PostMapping(value = "/game-app-simple/join", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public RealtimeResponse register(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType(APPLICATION_JSON);
		System.out.println("JOIN REQUEST");
		response.setCharacterEncoding(UTF_8);
		RealtimeResponse responseObject = realtimeUserService.registerUser(request);
		responseObject.setEntities(realtimeUserService.getPlayers(request.getParameter("server")));
		join2(responseObject);
		return responseObject;
	}

	@PostMapping(value = "/game-app-simple/server", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public RealtimeResponse serverlist(HttpServletRequest request, HttpServletResponse response) throws IOException {

		RealtimeResponse responseObject = new RealtimeResponse();
		responseObject.setAvailableServers(gameSettingService.getServerList());
		return responseObject;
	}

	// @MessageMapping("/move")
	// @SendTo("/wsResp/players")
	public RealtimeResponse join2(RealtimeResponse response) throws IOException {
		webSocket.convertAndSend("/wsResp/players", response);
		return response;
	}

	@MessageMapping("/addUser")
	@SendTo("/wsResp/players")
	public RealtimeResponse join(RealtimeRequest request) throws IOException {

		return realtimeUserService.connectUser(request);
	}

	@MessageMapping("/move")
	// @SendTo("/wsResp/players")
	public void move(RealtimeRequest request) throws IOException {
//		log.info("MOVE: {},",request);
		realtimeUserService.update(request);
	}

	@MessageMapping("/leave")
	@SendTo("/wsResp/players")
	public RealtimeResponse leave(RealtimeRequest request) throws IOException {

		return realtimeUserService.disconnectUser(request);
	}

	@MessageMapping("/chat")
	@SendTo("/wsResp/players")
	public RealtimeResponse send(Message message) {
		RealtimeResponse response = new RealtimeResponse();
		System.out.println("Message > " + message);
		String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
		OutputMessage msg = new OutputMessage(message.getFrom(), message.getText(), time);
		System.out.println("Output > " + msg);
		response.setMessage(msg);
		return response;
	}

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
