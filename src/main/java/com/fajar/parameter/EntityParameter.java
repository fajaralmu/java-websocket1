package com.fajar.parameter;

import java.util.ArrayList;
import java.util.List;

import com.fajar.util.JSONUtil;

public class EntityParameter {
 
	public static final Integer WIN_H = 700;
	public static final Integer WIN_W = 1200;
	public static final Integer baseHealth = 30;
	public static final Integer ROLE_PLAYER = 100;
	public static final Integer ROLE_BONUS_LIFE = 101;
	public static final Integer ROLE_BONUS_ARMOR = 103;
	public static final Integer ROLE_LAYOUT_1 = 102;
	public static List<Integer> roles() {
		 return new ArrayList<>(List.of(
				ROLE_PLAYER,ROLE_BONUS_LIFE,ROLE_BONUS_ARMOR,ROLE_LAYOUT_1
		));
	}
	
	public static void main(String[] dfdf) {
		System.out.println(JSONUtil.listToJson(roles()));
	}
}
