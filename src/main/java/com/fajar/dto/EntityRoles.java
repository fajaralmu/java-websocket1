package com.fajar.dto;

import java.util.Arrays;
import java.util.List;

public enum EntityRoles {
	ROLE_PLAYER,
	ROLE_BONUS_LIFE,
	ROLE_BONUS_ARMOR,
	ROLE_LAYOUT_1,
	ROLE_ROAD_LEFT,
	ROLE_ROAD_RIGHT,
	ROLE_ROAD_UP,
	ROLE_ROAD_DOWN,
	ROLE_FINISH_LINE,
	ROLE_STAGE;

	public static List<EntityRoles> roles() {
		 
		return Arrays.asList(EntityRoles.values());
	}

}
