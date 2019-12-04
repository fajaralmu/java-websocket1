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
		for (Entity entity : players) {
			if (entity.getStageId() <= stage) {
				stage = entity.getStageId();
			}
		} 
		return stage;
	}

	public static void printArray(List list) {
		for (Object object : list) {
			System.out.println("-" + object);
		}
	}

	static <T> List<T> singletonList(T obj) {
		List<T> list = new ArrayList<>();
		list.add(obj);
		return list;

	}

	public List<Entity> sortPlayer(List<Entity> players) {
		List<Entity> playerSortedByStage = new ArrayList<Entity>();
		List<Entity> finalSortedPlayer = new ArrayList<>();
		List<Entity> playerCalculate = players;
		Map<Integer, List<Entity>> groupedPlayerByStage = new HashMap<>();

		int maxStage = getMaxStage(players);

		int minStage = getMinStage(players);

		System.out.println("Stage: " + minStage + "-" + maxStage);
		for (int i = maxStage; i >= minStage; i--) {
			for (Entity player : players) {
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

			for (Entity entity : sorted) {
				Entity playerLayout = layoutService.getLayoutById(entity.getLayoutId());

				/**
				 * Update Lap
				 */
				boolean isFinishv2 = entity.getStagesPassed().size() == layoutService.getStagesCount()
						&& entity.getStageId() == layoutService.getMinStage();

				if (isFinishv2) {
					entity.setLap(entity.getLap() + 1);
					entity.setStagesPassed(new ArrayList<>());
				}
				playerSortedByStage.add(entity);
			}
		}

		
		int minLap = getMinLap(playerSortedByStage);
		int maxLap = getMaxLap(playerSortedByStage);

		for (int i = maxLap; i >= minLap; i--) {
			for (Entity player : playerSortedByStage) {
				if (player.getLap() == i) {
					finalSortedPlayer.add(player);
				}
			}
		}

		return finalSortedPlayer;

	}

	public int getMinLap(List<Entity> players) {
		int minLap = getMaxLap(players);
		for (Entity entity : players) {
			if (entity.getLap() < minLap) {
				minLap = entity.getLap();
			}
		}
		return minLap;
	}

	public int getMaxLap(List<Entity> players) {
		int maxLap = 0;
		for (Entity entity : players) {
			if (entity.getLap() > maxLap) {
				maxLap = entity.getLap();
			}
		}
		return maxLap;
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
			System.out.println(new Date() + "role down");
			side = SIDE_VERTICAL;
		} else if (stageRole == EntityParameter.ROLE_ROAD_UP) {
			// TODO: minY
			ROLE = EntityParameter.ROLE_ROAD_UP;
			System.out.println(new Date() + "role up");
			side = SIDE_VERTICAL;
		} else if (stageRole == EntityParameter.ROLE_ROAD_LEFT) {
			// TODO: minX
			System.out.println(new Date() + "role L");
			ROLE = EntityParameter.ROLE_ROAD_LEFT;
		} else if (stageRole == EntityParameter.ROLE_ROAD_RIGHT) {
			// TODO: maxX
			System.out.println(new Date() + "role R");
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
			} else if (ROLE == EntityParameter.ROLE_ROAD_RIGHT) {
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
			} else if (ROLE == EntityParameter.ROLE_ROAD_DOWN) {
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
