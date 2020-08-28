package com.fajar.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.dto.Entity;

@Service
public class EntityRepository {

	private final Map<String, List<Entity>> entities = new  HashMap<String, List<Entity>>();
	
	@Autowired
	private GameSettingService gameSettingService;
	
	@PostConstruct
	public void init() {
		List<String>servers = gameSettingService.getServerList();
		for (String string : servers) {
			entities.put(string, new ArrayList<Entity>());
		}
	}

	public synchronized void addUser(Entity user,final String server) {
		if (getPlayerByName(user.getName(), server) == null)
		{
			entities.get(server).add(user);
		}
	}

	public List<Entity> getPlayers(String server) {
		return entities.get(server);
	}

	/**
	 * get player by ID
	 * 
	 * @param id
	 * @return
	 */
	public Entity getPlayerByID(Integer id, String server) {
		for (Entity user : entities.get(server)) {
			if (user.getId().equals(id)) {
				return user;
			}
		}
		return null;
	}

	/**
	 * get player by Name
	 * 
	 * @param name
	 * @return
	 */
	public Entity getPlayerByName(String name, String server) {
		for (Entity user : entities.get(server)) {
			if (user.getName().equals(name)) {
				return user;
			}
		}
		return null;
	}

	/**
	 * remove player By ID
	 * 
	 * @param id
	 */
	public void removePlayer(Integer id,String server) {
		for (Entity realtimeUser : entities.get(server)) {
			if (realtimeUser.getId().equals(id)) {
				entities.get(server).remove(realtimeUser);
				break;
			}
		}
	}

	/**
	 * remove by role
	 * 
	 * @param role
	 */
	public synchronized void removeByRole(Integer role,String server) {
		List<Entity> playerList = new ArrayList<>();
		playerList.addAll(entities.get(server));
		for (Entity player : playerList) {
			if (player.getPhysical().getRole().equals(role)) {
				removePlayer(player.getId(),server);
			}
		}
	}

	/**
	 * add to list
	 * @param entity
	 */
	public void add(Entity entity,String server) {
		if (entities.get(server) == null) {
			entities.put(server, new ArrayList<Entity>());
		}
		this.entities.get(server).add(entity);
	}

	public void setPlayers(List<Entity> sortedEntities, String server) { 
		this.entities.put(server,sortedEntities);
	}

}
