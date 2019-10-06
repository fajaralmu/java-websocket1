package com.fajar.controller;

import static com.fajar.parameter.RestParameter.APPLICATION_JSON;
import static com.fajar.parameter.RestParameter.UTF_8;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.fajar.dto.RealtimeRequest;
import com.fajar.dto.RealtimeResponse;
import com.fajar.dto.Message;
import com.fajar.dto.OutputMessage;
import com.fajar.dto.RealtimeUser;
import com.fajar.service.RealtimeUserService;
import com.fajar.util.JSONUtil;

@CrossOrigin
@Controller
public class SocketController {
	Logger log = LoggerFactory.getLogger(SocketController.class);
	
	@Autowired
	RealtimeUserService realtimeUserService;
	
	public SocketController() {
		log.info("------------------SOCKET CONTROLLER #1-----------------");
	}
	
	@PostMapping(value="/chat-simple/join", consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	public void register( HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType(APPLICATION_JSON);
		response.setCharacterEncoding(UTF_8);
		RealtimeResponse responseObject = realtimeUserService.registerUser(request);
		response.getWriter().write(JSONUtil.objectToJson(responseObject));
	}
	
	@MessageMapping("/addUser")
	@SendTo("/wsResp/messages")
	public RealtimeResponse join( RealtimeRequest request) throws IOException {
		
		return realtimeUserService.connectUser(request);
	}
	
	@MessageMapping("/move")
	@SendTo("/wsResp/messages")
	public RealtimeResponse move( RealtimeRequest request) throws IOException {
		//log.info("MOVE: {},",request);
		return realtimeUserService.move(request);
	}
	
	@MessageMapping("/leave")
	@SendTo("/wsResp/messages")
	public RealtimeResponse leave( RealtimeRequest request) throws IOException {
		
		return realtimeUserService.disconnectUser(request);
	}
	
	
	
	@MessageMapping("/chat")
	@SendTo("/wsResp/messages")
	public RealtimeResponse send(Message message){
		RealtimeResponse response = new RealtimeResponse();
		System.out.println("Message > "+message);
	    String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
	    OutputMessage msg =new  OutputMessage(message.getFrom(), message.getText(), time);
	    System.out.println("Output > "+msg);
	    response.setMessage(msg);
	    return response;
	}
	
	
	
}
