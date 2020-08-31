package com.fajar.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fajar.dto.Entity;

public class CollectionUtil {

	public static <K, V> List<V> mapToList(Map<K, V> map) {

		List<V> list = new ArrayList<V>();

		for (K key : map.keySet()) {
			list.add(map.get(key));
		}

		return list;
	}

	public static LinkedHashMap<Integer, Entity> listToLinkedHashMap(List<Entity> sortedEntities) {

		LinkedHashMap<Integer, Entity> map = new LinkedHashMap<>();
		for (int i = 0; i < sortedEntities.size(); i++) {
			map.put(sortedEntities.get(i).getId(), sortedEntities.get(i));
		}
		return map;
	}
}
