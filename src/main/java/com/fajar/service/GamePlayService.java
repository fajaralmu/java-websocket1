package com.fajar.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.dto.CoordinateModule;
import com.fajar.dto.Entity;
import com.fajar.dto.EntityRoles;
import com.fajar.dto.RoadSide;

/**
 * this class handles race logic
 * 
 * @author Republic Of Gamers
 *
 */
@Service
public class GamePlayService {

	@Autowired
	private LayoutService layoutService;
	@Autowired
	private EntityRepository entityRepository; 

	public GamePlayService() {

	}

//	public static void main(String[] args) {
//		GamePlayService.printArray(new GamePlayService().sortPlayer(players()));
//	}
//
//	public static List<Entity> players() {
//		List<Entity> players = new ArrayList<>();
//		players.add(Entity.builder().id(22).stageId(1).build());
//		players.add(Entity.builder().id(33).stageId(5).build());
//		players.add(Entity.builder().id(54).stageId(3).build());
//		players.add(Entity.builder().id(67).stageId(4).build());
//		players.add(Entity.builder().id(12).stageId(6).build());
//		players.add(Entity.builder().id(13).stageId(7).build());
//		players.add(Entity.builder().id(11).stageId(9).build());
//		players.add(Entity.builder().id(42).stageId(11).build());
//		players.add(Entity.builder().id(32).stageId(2).build());
//		players.add(Entity.builder().id(2).stageId(5).build());
//		return players;
//	}

	private static int getMaxStage(Map<Integer, Entity> players) {
		int maxStage = 0;
		for (Entry<Integer, Entity> e : players.entrySet()) {
			Entity entity = e.getValue();
			if (entity.getStageId() > maxStage) {
				maxStage = entity.getStageId();
			}
		}
		return maxStage;
	}

	private static int getMinStage(Map<Integer, Entity> players) {
		int stage = Integer.MAX_VALUE;// getMaxStage(players);
		for (Entry<Integer, Entity> e : players.entrySet()) {
			Entity entity = e.getValue();
			if (entity.getStageId() <= stage) {
				stage = entity.getStageId();
			}
		}
		return stage;
	}

	static <T> List<T> singletonList(T obj) {
		List<T> list = new ArrayList<>();
		list.add(obj);
		return list;

	}

	public LinkedHashMap<Integer, Entity> calculateAndSortPlayer(String serverName) {
		Map<Integer, Entity> players = entityRepository.getPlayers(serverName);
		List<Entity> playerSortedByStage = new ArrayList<Entity>();
		LinkedHashMap<Integer, Entity> finalSortedPlayer = new LinkedHashMap<>();
		Map<Integer, List<Entity>> groupedPlayerByStage = new HashMap<>();

		int maxStage = getMaxStage(players);
		int minStage = getMinStage(players);

		// System.out.println("Stage: " + minStage + "-" + maxStage);
		for (int i = maxStage; i >= minStage; i--) {

			for (Entry<Integer, Entity> e : players.entrySet()) {
				Entity player = e.getValue();

				if (player.getStageId() == (i)) {
					if (groupedPlayerByStage.get(i) == null) {
						groupedPlayerByStage.put(i, singletonList(player));
					} else {
						groupedPlayerByStage.get(i).add(player);
					}

				}
			}

		}
		for (int i = maxStage; i >= minStage; i--) {
			if (groupedPlayerByStage.get(i) == null)
				continue;
			List<Entity> sorted = sortPlayerInSameStage(groupedPlayerByStage.get(i), i);

			for (int p = 0; p < sorted.size(); p++) {
				Entity entity = sorted.get(p);
//				Entity playerLayout = layoutService.getLayoutById(entity.getLayoutId());

				/**
				 * Update Lap
				 */
				if (isFinishv2(entity)) {
					entity.setLap(entity.getLap() + 1);
					entity.setStagesPassed(new ArrayList<>());
				}
				playerSortedByStage.add(entity);
			}
		}

		int minLap = getMinLap(playerSortedByStage);
		int maxLap = getMaxLap(playerSortedByStage);

		for (int i = maxLap; i >= minLap; i--) {
			for (int p = 0; p < playerSortedByStage.size(); p++) {
				Entity player = playerSortedByStage.get(p);
				if (player.getLap() == i) {
					finalSortedPlayer.put(player.getId(), player);
				}
			}
		}

		return finalSortedPlayer;

	}

	private boolean isFinishv2(Entity entity) {
		return entity.getStagesPassed().size() == layoutService.getStagesCount()
				&& entity.getStageId() == layoutService.getMinStage();
	}

	public int getMinLap(List<Entity> players) {
		int minLap = Integer.MAX_VALUE;// getMaxLap(players);
		for (int p = 0; p < players.size(); p++) {
			Entity entity = players.get(p);
			if (entity.getLap() < minLap) {
				minLap = entity.getLap();
			}
		}
		return minLap;
	}

	public int getMaxLap(List<Entity> players) {
		int maxLap = 0;
		for (int p = 0; p < players.size(); p++) {
			Entity entity = players.get(p);
			if (entity.getLap() > maxLap) {
				maxLap = entity.getLap();
			}
		}
		return maxLap;
	}

	public List<Entity> sortPlayerInSameStage(List<Entity> players, Integer stageId) {
		List<Entity> resultPlayer = new ArrayList<Entity>();

		RoadSide side = RoadSide.SIDE_HORIZONTAL;

		final EntityRoles stageRole = layoutService.getStageRole(stageId);
		EntityRoles ROLE = EntityRoles.ROLE_LAYOUT_1;
		if (stageRole == EntityRoles.ROLE_ROAD_DOWN) {
			// TODO: maxY
			ROLE = EntityRoles.ROLE_ROAD_DOWN;
			side = RoadSide.SIDE_VERTICAL;
		} else if (stageRole == EntityRoles.ROLE_ROAD_UP) {
			// TODO: minY
			ROLE = EntityRoles.ROLE_ROAD_UP;
			side = RoadSide.SIDE_VERTICAL;
		} else if (stageRole == EntityRoles.ROLE_ROAD_LEFT) {
			// TODO: minX
			ROLE = EntityRoles.ROLE_ROAD_LEFT;
		} else if (stageRole == EntityRoles.ROLE_ROAD_RIGHT) {
			// TODO: maxX
			ROLE = EntityRoles.ROLE_ROAD_RIGHT;
		}
		// System.out.println(new Date() +"Role:"+ROLE);
		int maxX = getMaxCoordinateVal(players, CoordinateModule.XPOS);
		int minX = getMinCoordinateVal(players, CoordinateModule.XPOS);
		int maxY = getMaxCoordinateVal(players, CoordinateModule.YPOS);
		int minY = getMinCoordinateVal(players, CoordinateModule.YPOS);
//		//System.out.println("PLAYERS: "+players.size());
		if (side == RoadSide. SIDE_HORIZONTAL) {
			if (ROLE == EntityRoles.ROLE_ROAD_LEFT) {
				for (int i = minX; i <= maxX; i++) {
					for (Entity player : players) {
						if (player.getPhysical().getX().equals(i)) {
							resultPlayer.add(player);
						}
					}

				}
			} else if (ROLE == EntityRoles.ROLE_ROAD_RIGHT) {
				for (int i = maxX; i >= minX; i--) {
					for (Entity player : players) {
						if (player.getPhysical().getX().equals(i)) {
							resultPlayer.add(player);
						}
					}

				}
			}

		} else if (side == RoadSide.SIDE_VERTICAL) {
			if (ROLE == EntityRoles.ROLE_ROAD_UP) {
				for (int i = minY; i <= maxY; i++) {
					for (Entity player : players) {
						if (player.getPhysical().getY().equals(i)) {
							resultPlayer.add(player);
						}
					}

				}
			} else if (ROLE == EntityRoles.ROLE_ROAD_DOWN) {
				for (int i = maxY; i >= minY; i--) {
					for (Entity player : players) {
						if (player.getPhysical().getY().equals(i)) {
							resultPlayer.add(player);
						}
					}

				}
			}

		}
//		//System.out.println("RESULT: "+resultPlayer.size());

		return resultPlayer;

	}

	
	/**
	 * get maximum coordinate value
	 * 
	 * @param players
	 * @param module
	 * @return
	 */
	private static int getMaxCoordinateVal(List<Entity> players, CoordinateModule module) {
		int maxVal = 0;
		for (Entity entity : players) {
			if (module.equals(CoordinateModule.XPOS)) {
				if (entity.getPhysical().getX() >= maxVal) {
					maxVal = entity.getPhysical().getX();
				}
			}
			if (module.equals(CoordinateModule.YPOS)) {
				if (entity.getPhysical().getY() >= maxVal) {
					maxVal = entity.getPhysical().getY();
				}
			}
		}
		return maxVal;
	}

	/**
	 * get minimum coordinate value
	 * 
	 * @param players
	 * @param module
	 * @return
	 */
	private static int getMinCoordinateVal(List<Entity> players, CoordinateModule module) {
		int maxVal = Integer.MAX_VALUE;// getMaxCoordinateVal(players, module);
		for (Entity entity : players) {
			if (module.equals(CoordinateModule.XPOS)) {
				if (entity.getPhysical().getX() <= maxVal) {
					maxVal = entity.getPhysical().getX();
				}
			}
			if (module.equals(CoordinateModule.YPOS)) {
				if (entity.getPhysical().getY() <= maxVal) {
					maxVal = entity.getPhysical().getY();
				}
			}
		}
		return maxVal;
	}

}
