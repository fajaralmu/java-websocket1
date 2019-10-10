package com.fajar.util;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fajar.annotation.Dto;
import com.fajar.dto.RealtimeResponse;
import com.fajar.dto.RealtimePlayer;

public class JSONUtil {

	public static void main(String[] aaa) {
		RealtimeResponse response = new RealtimeResponse();
		response.setResponseCode("00");
		response.setResponseMessage("OK");
		RealtimePlayer user = new RealtimePlayer();
		user.setId(1);
		user.setName("FAJAR AM");
		user.setJoinedDate(new Date());
		response.setUser(user);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("val1", "1111");
		map.put("val2", 2);
		map.put("USERRR", user);
		response.setInfo(map);
		response.getUsers().add(user);
		response.getUsers().add(user);
		response.getInfos().add("AAAA");
		response.getInfos().add("0000");
		System.out.println(objectToJson(response));
	}

	public static String objectToJson(Object o) {
		String json = "{";
		Class clazz = o.getClass();
		if (clazz.getAnnotation(Dto.class) == null) {
			return "\""+ o.toString() +"\"";
		}
		Field[] fields = clazz.getDeclaredFields();
		int fieldCount = fields.length;
		int counter = 0;
		for (Field field : fields) {
			String key = "\"" + field.getName() + "\"";
			String value = null;
			Class fieldType = field.getType();
			field.setAccessible(true);
			try {
				if (fieldType != List.class && field.get(o) != null && fieldType != Map.class && fieldType.getAnnotation(Dto.class) == null) {
					value = "\"" + field.get(o).toString() + "\"";
				} else if (fieldType != List.class && field.get(o) != null && fieldType != Map.class
						&& fieldType.getAnnotation(Dto.class) != null) {
					value = objectToJson(field.get(o));
				} else if (field.get(o) != null && fieldType == Map.class) {
					value = mapToJson((Map<String, Object>) field.get(o));
				}  else if(field.get(o) != null &&fieldType == List.class) {
					value = listToJson((List) field.get(o));;
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			json += key + ":" + value;
			if (counter < fieldCount - 1) {
				json += ",";
			}
			counter++;

		}
		json += "}";
		return json;
	}

	public static String mapToJson(Map<String, Object> o) {
		String json = "{";
		Set<String> fields = o.keySet();
		int fieldCount = fields.size();
		int counter = 0;
		for (String field : fields) {
			String key = "\"" + field + "\"";
			String value = null;
			Object mapvalue = o.get(field);
			if (mapvalue != null) {
				Class fieldType = mapvalue.getClass();

				if (fieldType != List.class && fieldType != Map.class && fieldType.getAnnotation(Dto.class) == null) {
					value = "\"" + mapvalue.toString() + "\"";
				} else if (fieldType != List.class && fieldType != Map.class && fieldType.getAnnotation(Dto.class) != null) {
					value = objectToJson(mapvalue);
				} else if (fieldType == Map.class) {
					value = mapToJson((Map<String, Object>) mapvalue);
				} else if(fieldType == List.class) {
					value = listToJson((List) mapvalue);;
				}

			}
			json += key + ":" + value;
			if (counter < fieldCount - 1) {
				json += ",";
			}
			counter++;

		}
		json += "}";
		return json;
	}

	public static String listToJson(List list) {
		String json = "[";
		
		for (int i = 0; i < list.size(); i++) {
			Object array_element = list.get(i);
			String value = null;
			if(array_element!=null) {
				value = objectToJson(array_element);
			}
			json+=value;
			if(i<list.size()-1) {
				json+=",";
			}
			
		}
		
		json+="]";
		return json;
	}
}
