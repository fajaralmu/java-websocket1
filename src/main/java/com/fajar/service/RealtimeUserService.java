package com.fajar.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.fajar.controller.SocketController;
import com.fajar.dto.RealtimeRequest;
import com.fajar.dto.RealtimeResponse;
import com.fajar.parameter.EntityParameter;
import com.fajar.dto.Entity;
import com.fajar.dto.OutputMessage;
import com.fajar.dto.RealtimePlayer;

@Service
public class RealtimeUserService {
	Logger log = LoggerFactory.getLogger(RealtimeUserService.class);
	
	private Integer bonusCount=0;
	private List<RealtimePlayer> users = new ArrayList<>();
	private Random random = new Random();
	private Long currentTime = new Date().getTime();
	private boolean isRegistering = false;
	
	@Autowired
	private SimpMessagingTemplate webSocket;
	
	public RealtimeUserService() {
		log.info("-----------------REALTIME SERVICE-------------------");
		startThread();
	}
	
	private void startThread() {
		currentTime = new Date().getTime();
		Thread thread  = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(true) {
					Long systemDate = new Date().getTime();
					Long delta =systemDate - currentTime;
					//System.out.println(delta+"______________________________");
					if(delta >= 8000 && isRegistering == false) {
						System.out.println("..............Adding new Bonus");
						addBonusLife();
						currentTime=systemDate;
					}
				}
			}
		});
		thread.start();
	}
	
	public synchronized void addUser(RealtimePlayer user) {
		users.add(user);
	}
	
	public List<RealtimePlayer> getUsers(){
		return users;
	}
	
	public RealtimePlayer getUser(Integer id) {
		for(RealtimePlayer user:users) {
			if(user.getId().equals(id)) {
				return user;
			}
		}
		return null;
	}

	public RealtimePlayer getUser(String name) {
		for(RealtimePlayer user:users) {
			if(user.getName().equals(name)) {
				return user;
			}
		}
		return null;
	}

	
	public synchronized RealtimeResponse registerUser(HttpServletRequest request) {
		isRegistering=true;
		RealtimeResponse responseObject = new RealtimeResponse();
		String name =request.getParameter("name");
		
		RealtimePlayer user;
		if(getUser(name)!=null) {
//			responseObject.setResponseCode("01");
//			responseObject.setResponseMessage("Please choose another name!");
//			return responseObject;
			user =getUser(name);
		}else {
			user = new RealtimePlayer(random.nextInt(100),name,new Date());
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
		isRegistering = false;
		return responseObject;
	}
	
	public void removePlayer(Integer id) {
		for (RealtimePlayer realtimeUser : users) {
			if(realtimeUser.getId().equals(id)) {
				users.remove(realtimeUser);
				break;
			}
		}
	}
	
	public void addBonusLife() {
		Random rand = new Random();
		RealtimePlayer bonus = new RealtimePlayer();
		bonus.setId(rand.nextInt(101010)+1);
		bonus.setActive(true);
		bonus.setName("Extra Life "+bonus.getId());
		Entity entity = new Entity();
		Integer x = rand.nextInt(EntityParameter.WIN_W);
		Integer y = rand.nextInt(EntityParameter.WIN_H);
		entity.setRole(EntityParameter.ROLE_BONUS_LIFE);
		entity.setPeriod(10000L);
		entity.setX(x);
		entity.setY(y);
		bonus.setEntity(entity);
		removeByRole(EntityParameter.ROLE_BONUS_LIFE);
		users.add(bonus);
		bonusCount++;
		RealtimeResponse response = new  RealtimeResponse("00","OK");
		response.setUsers(users);
		webSocket.convertAndSend("/wsResp/players", response);
		
	}
	
	private synchronized void removeByRole(Integer role) {
		List<RealtimePlayer> playerList = new ArrayList<>();
		playerList.addAll(users);
		for(RealtimePlayer player:playerList) {
			System.out.println(playerList.size()+"**************************************************");
			if(player.getEntity().getRole().equals(role)) {
			//	System.out.println("cc---------------------------------------------");
				removePlayer(player.getId());
			}
		}
	}

	public RealtimeResponse disconnectUser(RealtimeRequest request) {
		Integer userId = request.getUser().getId();
		RealtimePlayer user = getUser(userId);
		log.info("REQ: {}",request);
		if(user == null) {
			RealtimeResponse response = new RealtimeResponse("01","Invalid USER!");
			response.setMessage(new OutputMessage("SYSTEM", "INVALID USER", new Date().toString()));
			return response;
		}
		removePlayer(userId);
		RealtimeResponse response = new RealtimeResponse("00","OK");
		response.setUser(user);
		response.setUsers(users);
		response.setMessage(new  OutputMessage(user.getName(), "Good bye! i'm leaving now", new Date().toString()));
		return response;
	}
	
	public RealtimeResponse connectUser(RealtimeRequest request) {
		// TODO Auto-generated method stub
		Integer userId = request.getUser().getId();
		RealtimePlayer user = getUser(userId);
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
		for(RealtimePlayer user:users) {
			if(user.getId().equals(request.getUser().getId())) {
				
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
