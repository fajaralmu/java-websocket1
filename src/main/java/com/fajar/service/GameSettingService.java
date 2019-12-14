package com.fajar.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class GameSettingService {

	
	
	public List<String> getServerList() {
		List<String> list = new ArrayList<String>();
		// TODO Auto-generated method stub
		list.add("Server_A");
		list.add("Server_B");
		list.add("Server_C");
		return list ;
	}

}
