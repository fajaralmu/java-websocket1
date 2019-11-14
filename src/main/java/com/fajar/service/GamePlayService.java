package com.fajar.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.dto.Entity;
import com.fajar.parameter.EntityParameter;

@Service
public class GamePlayService {

	@Autowired
	private LayoutService layoutService;

	public GamePlayService() {

	}

	public static void main(String[] args) { 
		GamePlayService.printArray(new GamePlayService().sortPlayer(players()));
	}

	public static List<Entity> players() {
		List<Entity> players = new ArrayList<>();
		players.add(Entity.builder().id(22).stageId(1).build());
		players.add(Entity.builder().id(33).stageId(5).build());
		players.add(Entity.builder().id(54).stageId(3).build());
		players.add(Entity.builder().id(67).stageId(4).build());
		players.add(Entity.builder().id(12).stageId(6).build());
		players.add(Entity.builder().id(13).stageId(7).build());
		players.add(Entity.builder().id(11).stageId(9).build());
		players.add(Entity.builder().id(42).stageId(11).build());
		players.add(Entity.builder().id(32).stageId(2).build());
		players.add(Entity.builder().id(2).stageId(5).build());
		return players;
	}

	private static int getMaxStage(List<Entity> players) {
		int maxStage = 0;
		for (Entity entity : players) {
			if (entity.getStageId() > maxStage) {
				maxStage = entity.getStageId();
			}
		}
		return maxStage;
	}

	private static int getMinStage(List<Entity> players) {
		int stage = getMaxStage(players);
//		System.out.println("MAX STAGE: "+stage);
		for (Entity entity : players) {
			if (entity.getStageId() <= stage) {
				stage = entity.getStageId();
			}
		}
//		System.out.println("MIN STAGE: "+stage);
		return stage;
	}

	public static void printArray(List list) {
		for (Object object : list) {
			System.out.println("-" + object);
		}
	}
	
	static <T> List<T> singletonList(T obj){
		List<T> list = new ArrayList<>();
		list.add(obj);
		return list;
		
	}

	public List<Entity> sortPlayer(List<Entity> players) {
		List<Entity> resultPlayer = new ArrayList<Entity>();
		List<Entity> playerCalculate = players;
		Map<Integer, List<Entity>> groupedPlayer = new HashMap<>();

		int maxStage = getMaxStage(players);
	
		int minStage = getMinStage(players);
		
		System.out.println("Stage: "+minStage+"-"+maxStage);
//		System.out.println("PLAYERS: "+players.size());
		for (int i = maxStage; i >= minStage; i--) {
			for (Entity player : players) {
				if (player.getStageId().equals(i)) {
					if (groupedPlayer.get(i) == null) {
						groupedPlayer.put(i, singletonList(player)); 
					} else {
						groupedPlayer.get(i).add(player);
					}
					
				}
			}

		}
//		System.out.println("RESULT: "+resultPlayer.size());
		for(int i = maxStage; i >= minStage; i--)  {
			if(groupedPlayer.get(i)== null)
				continue;
			List<Entity> sorted = sortPlayerInSameStage(groupedPlayer.get(i), i);
			//resultPlayer.addAll(sorted);
//			System.out.println(i+")will add: "+sorted.size());
			for (Entity entity : sorted) {
				resultPlayer.add(entity);
			}
		}

		return resultPlayer;

	}

	static final int SIDE_HORIZONTAL = 0x22, SIDE_VERTICAL = 0x33;

	public List<Entity> sortPlayerInSameStage(List<Entity> players, Integer stageId) {
		List<Entity> resultPlayer = new ArrayList<Entity>();
		
		int side = SIDE_HORIZONTAL;

		final int stageRole = layoutService.getStageRole(stageId);
		int ROLE = 0;
		if (stageRole == EntityParameter.ROLE_ROAD_DOWN) {
			// TODO: maxY
			ROLE = EntityParameter.ROLE_ROAD_DOWN;
			System.out.println(new Date()+"role down");
			side = SIDE_VERTICAL;
		}else
		if (stageRole == EntityParameter.ROLE_ROAD_UP) {
			// TODO: minY
			ROLE = EntityParameter.ROLE_ROAD_UP;
			System.out.println(new Date()+"role up");
			side = SIDE_VERTICAL;
		}else
		if (stageRole == EntityParameter.ROLE_ROAD_LEFT) {
			// TODO: minX
			System.out.println(new Date()+"role L");
			ROLE = EntityParameter.ROLE_ROAD_LEFT;
		}else
		if (stageRole == EntityParameter.ROLE_ROAD_RIGHT) {
			// TODO: maxX
			System.out.println(new Date()+"role R");
			ROLE = EntityParameter.ROLE_ROAD_RIGHT;
		}

		int maxX = getMaxVal(players, XPOS);
		int minX = getMinVal(players, XPOS);
		int maxY = getMaxVal(players, YPOS);
		int minY = getMinVal(players, YPOS);
//		System.out.println("PLAYERS: "+players.size());
		if (side == SIDE_HORIZONTAL) {
			if (ROLE == EntityParameter.ROLE_ROAD_LEFT) {
				for (int i = minX; i <= maxX; i++) {
					for (Entity player : players) {
						if (player.getPhysical().getX().equals(i)) {
							resultPlayer.add(player);
						}
					}

				}
			}else
			if (ROLE == EntityParameter.ROLE_ROAD_RIGHT) {
				for (int i = maxX; i >= minX; i--) {
					for (Entity player : players) {
						if (player.getPhysical().getX().equals(i)) {
							resultPlayer.add(player);
						}
					}

				}
			}

		} else if (side == SIDE_VERTICAL) {
			if (ROLE == EntityParameter.ROLE_ROAD_UP) {
				for (int i = minY; i <= maxY; i++) {
					for (Entity player : players) {
						if (player.getPhysical().getY().equals(i)) {
							resultPlayer.add(player);
						}
					}

				}
			}else
			if (ROLE == EntityParameter.ROLE_ROAD_DOWN) {
				for (int i = maxY; i >= minY; i--) {
					for (Entity player : players) {
						if (player.getPhysical().getY().equals(i)) {
							resultPlayer.add(player);
						}
					}

				}
			}

		}
//		System.out.println("RESULT: "+resultPlayer.size());

		return resultPlayer;

	}

	static final int XPOS = 1, YPOS = 2;

	private static int getMaxVal(List<Entity> players, int module) {
		int maxVal = 0;
		for (Entity entity : players) {
			if (module == XPOS) {
				if (entity.getPhysical().getX() >= maxVal) {
					maxVal = entity.getPhysical().getX();
				}
			}
			if (module == YPOS) {
				if (entity.getPhysical().getY() >= maxVal) {
					maxVal = entity.getPhysical().getY();
				}
			}
		}
		return maxVal;
	}

	private static int getMinVal(List<Entity> players, int module) {
		int maxVal = getMaxVal(players, module);
		for (Entity entity : players) {
			if (module == XPOS) {
				if (entity.getPhysical().getX() <= maxVal) {
					maxVal = entity.getPhysical().getX();
				}
			}
			if (module == YPOS) {
				if (entity.getPhysical().getY() <= maxVal) {
					maxVal = entity.getPhysical().getY();
				}
			}
		}
		return maxVal;
	}

}
