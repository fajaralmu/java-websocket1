package com.fajar.service;

import static com.fajar.util.CollectionUtil.mapToList;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.fajar.dto.Entity;
import com.fajar.dto.OutputMessage;
import com.fajar.dto.Physical;
import com.fajar.dto.RealtimeRequest;
import com.fajar.dto.RealtimeResponse;
import com.fajar.util.ThreadUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RealtimeService {

//	private Integer bonusCount = 0;
//	private List<Entity> entities = new ArrayList<>(); 
	private Random random = new Random();
	private Long currentTime = new Date().getTime();
	private Boolean isRegistering = false;
	private Long deltaTime = 8000L;

	private final GamePlayService gamePlayService;
	private final SimpMessagingTemplate webSocket;
	private final LayoutService layoutService;
	private final EntityRepository entityRepository;

	@Autowired
	public RealtimeService(GamePlayService gamePlayService, SimpMessagingTemplate webSocket,
			LayoutService layoutService, EntityRepository entityRepository) {
		super();
		this.gamePlayService = gamePlayService;
		this.webSocket = webSocket;
		this.layoutService = layoutService;
		this.entityRepository = entityRepository;
		log.info(":: RealtimeService CONSTRUCTOR ::");
		System.out.println("Realtime Service Constructor");
	}

	@PostConstruct
	private void loadLayout() {
		System.out.println("WILL loadLayout");
		layoutService.load();

	}

	public List<Entity> getLayouts() {
		return layoutService.getLayouts();
	}

//	private void startThread() {
//		currentTime = new Date().getTime();
//		Thread thread = new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				while (true) {
//					Long systemDate = new Date().getTime();
//					Long delta = systemDate - currentTime;
//					if (delta >= deltaTime && isRegistering == false) {
//						// addBonusLife();
//						currentTime = systemDate;
//					}
//				}
//			}
//		});
//		thread.start();
//	}

//	private boolean intersectLayout(Entity player) {
//		for (Entity layoutItem : layoutService.getLayouts()) {
//			if (Physical.intersect(player, layoutItem))
//				return true;
//		}
//		return false;
//	}

	public synchronized RealtimeResponse registerUser(HttpServletRequest request) {
		if (null == request.getParameter("name") || request.getParameter("name").trim().equals("")) {
			return RealtimeResponse.failed("invalid Name");
		}

		isRegistering = true;

		final String name = request.getParameter("name").replace(" ", "_");
		final String serverName = request.getParameter("server");

		System.out.println("Will Join NAME:" + name + ",SERVER:" + serverName);

		Entity user = entityRepository.getPlayerByName(name, serverName);
		String responseMessage;
		if (user == null) {
			user = generateNewUser(name);
			responseMessage = name + " Successfully joined";
		} else {
			responseMessage = "Welcome back, " + name + " !";
		}

		entityRepository.addUser(user, serverName);

		RealtimeResponse responseObject = new RealtimeResponse();
		responseObject.setResponseCode("00");
		responseObject.setResponseMessage(responseMessage);
		responseObject.setEntity(user);
		responseObject.setEntities(entityRepository.getPlayersAsList(serverName));

		sendResponse(serverName, responseObject);

		isRegistering = false;

		System.out.println("----------------------REGISTER USER: " + user);

		return responseObject;
	}

	private Entity generateNewUser(String name) {
		Entity user = new Entity(random.nextInt(100), name, new Date());
		user.setStageId(layoutService.getMinStage());
		user.setLayoutId(0);
		Physical physical = getDefaultPhysical();
		user.setPhysical(physical);
		return user;
	}

	private Physical getDefaultPhysical() {
		Physical physical = new Physical();
		physical.setX(layoutService.getStartX());
		physical.setY(layoutService.getStartY());
		physical.setH(30);
		physical.setW(30);
		physical.setColor("rgb(" + random.nextInt(200) + "," + random.nextInt(200) + "," + random.nextInt(200) + ")");
		return physical;
	}

//	public void addBonusLifes() {
//		Random rand = new Random();
//		Entity bonus = new Entity();
//		bonus.setId(rand.nextInt(101010) + 1);
//		bonus.setActive(true);
//		bonus.setName("Extra Life " + bonus.getId());
//		bonus.setLife(rand.nextInt(9) + 1);
//		Physical entity = new Physical();
//		Integer x = rand.nextInt(EntityParameter.WIN_W - entity.getW());
//		Integer y = rand.nextInt(EntityParameter.WIN_H - entity.getH());
//		entity.setRole(EntityParameter.ROLE_BONUS_LIFE);
//		entity.setPeriod(10000L);
//		entity.setX(x);
//		entity.setY(y);
//		bonus.setPhysical(entity);
//		entityRepository.removeByRole(EntityParameter.ROLE_BONUS_LIFE);
//		if (intersectLayout(bonus)) {
//			return;
//		}
//		entityRepository.add(bonus);
//		bonusCount++;
//		RealtimeResponse response = new RealtimeResponse("00", "OK");
//		response.setEntities(entityRepository.getPlayers());
//		log.info("..............Adding new Bonus :{}", bonus);
//
//		webSocket.convertAndSend("/wsResp/players", response);
//
//	}

	public RealtimeResponse disconnectUser(RealtimeRequest request) {
		Integer userId = request.getEntity().getId();
		Entity user = entityRepository.getPlayerByID(userId, request.getServerName());
		log.info("REQ: {}", request);
		/*
		 * if(user == null) { RealtimeResponse response = new
		 * RealtimeResponse("01","Invalid USER!"); response.setMessage(new
		 * OutputMessage("SYSTEM", "INVALID USER", new Date().toString())); return
		 * response; }
		 */
		entityRepository.removePlayer(userId, request.getServerName());
		RealtimeResponse response = new RealtimeResponse("00", "OK");
		response.setEntity(user);
		response.setEntities(entityRepository.getPlayersAsList(request.getServerName()));

		if (user != null) {
			response.setMessage(new OutputMessage(user.getName(), "Good bye! i'm leaving now", new Date().toString()));
		}

		sendResponse(request.getServerName(), response);
		return response;
	}

	public RealtimeResponse connectUser(RealtimeRequest request) {
		Integer userId = request.getEntity().getId();
		Entity user = entityRepository.getPlayerByID(userId, request.getServerName());
		log.info("REQ: {}", request);

		if (user == null) {
			RealtimeResponse response = RealtimeResponse.failed("Invalid USER!");
			response.setMessage(new OutputMessage("SYSTEM", "INVALID USER", new Date().toString()));
			return response;
		}

		RealtimeResponse response = new RealtimeResponse("00", "OK");
		response.setEntity(user);
		response.setServerName(request.getServerName());
		response.setMessage(new OutputMessage(user.getName(), "HI!, i'm joining conversation!", new Date().toString()));
		return response;
	}

	private static void updateFromRequest(Entity entity, Entity requestEntity) {
		entity.getPhysical().setLastUpdated(new Date());
		entity.setStagesPassed(requestEntity.getStagesPassed());
		entity.setPhysical(requestEntity.getPhysical());
		entity.setMissiles(requestEntity.getMissiles());
		entity.setLife(requestEntity.getLife());
		entity.setLap(requestEntity.getLap());
		entity.setActive(requestEntity.isActive());
		entity.setForceUpdate(requestEntity.isForceUpdate());
		entity.setStage();
	}

	public synchronized void update(RealtimeRequest request) {

		ThreadUtil.run(() -> {
			final String serverName = request.getServerName();
			final int entityId = request.getEntity().getId();
			Entity entity = entityRepository.getPlayerByID(entityId, serverName); 
			
			try {
				int layoutId = request.getEntity().getLayoutId(); 
				int stageId = layoutService.getLayoutById(layoutId).getStageId();
				entity.setStageId(stageId);
				entity.setLayoutId(layoutId);
				
			} catch (Exception ex) { 
				entity.setStageId(0);
			}

			updateFromRequest(entity, request.getEntity());

			entityRepository.updateUser(entity, serverName);

			calculatePositionAndSend(serverName);

		});

	}

	public String getJsonListOfLayouts() {
		return layoutService.getJsonListOfLayouts();
	}

	public synchronized void resetPosition(RealtimeRequest request) {
		int entityId = request.getEntity().getId();

		System.out.println("RESET POSITION " + entityId);

		ThreadUtil.run(() -> {

			String serverName = request.getServerName();
			entityReset(entityId, serverName);
			calculatePositionAndSend(serverName);

			System.out.println("Success Reset Position");
		});

	}

	private Entity entityReset(int entityId, String serverName) {
		Entity e = entityRepository.getPlayerByID(entityId, serverName);

		e.getPhysical().setX(layoutService.getStartX());
		e.getPhysical().setY(layoutService.getStartY());
		e.setForceUpdate(Boolean.TRUE);
		e.setBreakLoop(true);

		entityRepository.updateUser(e, serverName);
		return e;
	}

	private void calculatePositionAndSend(String serverName) {

		LinkedHashMap<Integer, Entity> sortedEntities = gamePlayService.calculateAndSortPlayer(serverName);
		entityRepository.setPlayers(sortedEntities, serverName);

		ThreadUtil.run(() -> {
			RealtimeResponse response = new RealtimeResponse("00", "OK", serverName, mapToList(sortedEntities));
			sendResponse(serverName, response);
		});
	}

	public List<Entity> getPlayers(String serverName) {

		return mapToList(entityRepository.getPlayers(serverName));
	}

	/**
	 * Send to topic : wsResp/players/{serverName}
	 * 
	 * @param serverName
	 * @param response
	 * @return
	 */
	public RealtimeResponse sendResponse(String serverName, RealtimeResponse response) {
		webSocket.convertAndSend("/wsResp/players/" + serverName, response);
		return response;
	}

}
