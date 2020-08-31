package com.fajar.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.dto.Entity;
import com.fajar.util.CollectionUtil;

@Service
public class EntityRepository {

	private final Map<String, Map<Integer, Entity>> entities = new HashMap<String, Map<Integer, Entity>>();

	@Autowired
	private GameSettingService gameSettingService;

	@PostConstruct
	public void init() {
		List<String> servers = gameSettingService.getServerList();
		for (String string : servers) {
			entities.put(string, new HashMap<>());
		}
	}

	public synchronized void addUser(Entity user, final String server) {
		checkServer(server);
		if (getPlayerByName(user.getName(), server) == null) {
			entities.get(server).put(user.getId(), user);
		}
	}

	public Map<Integer, Entity> getPlayers(String server) {
		checkServer(server);
		return entities.get(server);
	}

	/**
	 * get player by ID
	 * 
	 * @param id
	 * @return
	 */
	public Entity getPlayerByID(Integer id, String server) {
		Map<Integer, Entity> entitymap = getEntityMap(server);
		if (entitymap != null) {
			return entitymap.get(id);
		}
		return null;
	}

	private Map<Integer, Entity> getEntityMap(String serverName) {
		return entities.get(serverName);
	}

	/**
	 * get player by Name
	 * 
	 * @param name
	 * @return
	 */
	public Entity getPlayerByName(String name, String server) {
		Map<Integer, Entity> entitymap = getEntityMap(server);
		for (Integer key : entitymap.keySet()) {
			if (entitymap.get(key).getName().equals(name)) {
				return entitymap.get(key);
			}
		}

		return null;
	}

	/**
	 * remove player By ID
	 * 
	 * @param id
	 */
	public synchronized void removePlayer(Integer id, String server) {
		try {
			entities.get(server).remove(id);
		} catch (Exception e) {

		}

	}

	/**
	 * remove by role
	 * 
	 * @param role
	 */
	public synchronized void removeByRole(Integer role, String serverName) {
		List<Entity> playerList = new ArrayList<>();
		playerList.addAll(CollectionUtil.mapToList(getEntityMap(serverName)));
		
		for (Entity player : playerList) {
			if (player.getPhysical().getRole().equals(role)) {
				removePlayer(player.getId(), serverName);
			}
		}
	}

	private void checkServer(String serverName) {
		if (entities.get(serverName) == null) {
			entities.put(serverName, new LinkedHashMap<>());
		}
	}

	public void setPlayers(List<Entity> sortedEntities, String server) {
		LinkedHashMap<Integer, Entity> entitiesMap = CollectionUtil.listToLinkedHashMap(sortedEntities);
		this.entities.put(server, entitiesMap);
	}

	public List<Entity> getPlayersAsList(String serverName) {
		Map<Integer, Entity> players = getPlayers(serverName);
		return CollectionUtil.mapToList(players);
	}

	public void updateUser(Entity entity, String serverName) {
		try {
			entities.get(serverName).put(entity.getId(), entity);
		}catch (Exception e) {
			e.printStackTrace();
//			System.out.println("Error updating entity");
		}
		
	}

}
