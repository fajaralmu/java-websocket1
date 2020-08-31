package com.fajar.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
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

import lombok.extern.slf4j.Slf4j;

@CrossOrigin
@RestController
@Slf4j
public class SocketController { 
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
		 
		RealtimeResponse responseObject = realtimeUserService.registerUser(request);
		String serverName =  request.getParameter("server");
		responseObject.setEntities( realtimeUserService.getPlayers(serverName ));
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
//		realtimeUserService.update(request);
		try{
			realtimeUserService.update (request);
		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	@MessageMapping("/leave")
	@SendTo("/wsResp/players")
	public RealtimeResponse leave(RealtimeRequest request) throws IOException {
		System.out.println("Leaving APP.........");
		return realtimeUserService.disconnectUser(request);
	}
	
	@MessageMapping("/resetposition") 
	public void resetPosition(RealtimeRequest request) throws IOException { 
		
		try{
			System.out.println("resetPosition.........");
			realtimeUserService.resetPosition(request);
		}catch (Exception e) {
			System.out.println("Error reset position..");
			e.printStackTrace();
		}
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

	
	
}
