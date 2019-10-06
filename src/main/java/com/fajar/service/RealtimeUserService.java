package com.fajar.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fajar.controller.SocketController;
import com.fajar.dto.RealtimeRequest;
import com.fajar.dto.RealtimeResponse;
import com.fajar.dto.Entity;
import com.fajar.dto.OutputMessage;
import com.fajar.dto.RealtimeUser;

@Service
public class RealtimeUserService {
	Logger log = LoggerFactory.getLogger(RealtimeUserService.class);
	
	private List<RealtimeUser> users = new ArrayList<>();
	Random random = new Random();
	
	
	public RealtimeUserService() {
		log.info("-----------------REALTIME SERVICE-------------------");
	}
	
	public void addUser(RealtimeUser user) {
		users.add(user);
	}
	
	public List<RealtimeUser> getUsers(){
		return users;
	}
	
	public RealtimeUser getUser(Integer id) {
		for(RealtimeUser user:users) {
			if(user.getId().equals(id)) {
				return user;
			}
		}
		return null;
	}

	public RealtimeUser getUser(String name) {
		for(RealtimeUser user:users) {
			if(user.getName().equals(name)) {
				return user;
			}
		}
		return null;
	}

	
	public RealtimeResponse registerUser(HttpServletRequest request) {
		RealtimeResponse responseObject = new RealtimeResponse();
		String name =request.getParameter("name");
		
		RealtimeUser user;
		if(getUser(name)!=null) {
//			responseObject.setResponseCode("01");
//			responseObject.setResponseMessage("Please choose another name!");
//			return responseObject;
			user =getUser(name);
		}else {
			user = new RealtimeUser(random.nextInt(100),name,new Date());
			Entity entity = new Entity();
			entity.setX(10);
			entity.setY(10);
			entity.setColor("rgb("+random.nextInt(200)+","+random.nextInt(200)+","+random.nextInt(200)+")");
			user.setEntity(entity);
			
		}
		responseObject.setResponseCode("00");
		responseObject.setResponseMessage("OK");
		
		
		addUser(user);
		responseObject.setUser(user);
		responseObject.setUsers(getUsers());
		return responseObject;
	}
	
	public void removeUser(Integer id) {
		for (RealtimeUser realtimeUser : users) {
			if(realtimeUser.getId().equals(id)) {
				users.remove(realtimeUser);
				break;
			}
		}
	}

	public RealtimeResponse disconnectUser(RealtimeRequest request) {
		Integer userId = request.getUser().getId();
		RealtimeUser user = getUser(userId);
		log.info("REQ: {}",request);
		if(user == null) {
			RealtimeResponse response = new RealtimeResponse("01","Invalid USER!");
			response.setMessage(new OutputMessage("SYSTEM", "INVALID USER", new Date().toString()));
			return response;
		}
		removeUser(userId);
		RealtimeResponse response = new RealtimeResponse("00","OK");
		response.setUser(user);
		response.setUsers(users);
		response.setMessage(new  OutputMessage(user.getName(), "Good bye! i'm leaving now", new Date().toString()));
		return response;
	}
	
	public RealtimeResponse connectUser(RealtimeRequest request) {
		// TODO Auto-generated method stub
		Integer userId = request.getUser().getId();
		RealtimeUser user = getUser(userId);
		log.info("REQ: {}",request);
		if(user == null) {
			RealtimeResponse response = new RealtimeResponse("01","Invalid USER!");
			response.setMessage(new OutputMessage("SYSTEM", "INVALID USER", new Date().toString()));
			return response;
		}
		RealtimeResponse response = new RealtimeResponse("00","OK");
		response.setUser(user);
		response.setMessage(new  OutputMessage(user.getName(), "HI!, i'm joining conversation!", new Date().toString()));
		return response;
	}

	public RealtimeResponse move(RealtimeRequest request) {
		RealtimeResponse response = new RealtimeResponse("00","OK");
		for(RealtimeUser user:users) {
			if(user.getId().equals(request.getUser().getId())) {
//				user.getEntity().setX(request.getUser().getEntity().getX());
//				user.getEntity().setY(request.getUser().getEntity().getY());
				user.setEntity(request.getUser().getEntity());
				user.setMissiles(request.getUser().getMissiles());
				user.setLife(request.getUser().getLife());
				user.setActive(request.getUser().isActive());
			}
		}
		response.setUsers(users);
		return response;
	}
	
	
}
