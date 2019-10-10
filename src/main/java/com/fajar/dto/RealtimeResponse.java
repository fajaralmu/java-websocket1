package com.fajar.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fajar.annotation.Dto;

@Dto
public class RealtimeResponse implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4025058368937979008L;
	private OutputMessage message;
	private RealtimePlayer user;
	private String responseCode;
	private String responseMessage;
	private Map<String, Object> info;
	private List<RealtimePlayer> users = new ArrayList<RealtimePlayer>();
	
	private List<String> infos = new ArrayList<>();

	public RealtimeResponse() {
		super();
	}
	public RealtimeResponse(String rc,String rm) {
		this.responseCode = rc;
		this.responseMessage = rm;
	}
	
	
	public OutputMessage getMessage() {
		return message;
	}

	public void setMessage(OutputMessage message) {
		this.message = message;
	}

	public RealtimePlayer getUser() {
		return user;
	}

	public void setUser(RealtimePlayer user) {
		this.user = user;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public Map<String, Object> getInfo() {
		return info;
	}

	public void setInfo(Map<String, Object> info) {
		this.info = info;
	}

	public List<RealtimePlayer> getUsers() {
		return users;
	}

	public void setUsers(List<RealtimePlayer> users) {
		this.users = users;
	}

	public List<String> getInfos() {
		return infos;
	}

	public void setInfos(List<String> infos) {
		this.infos = infos;
	}
	
	
	

}
