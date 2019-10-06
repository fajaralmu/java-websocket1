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
	private RealtimeUser user;
	private String responseCode;
	private String responseMessage;
	private Map<String, Object> info;
	private List<RealtimeUser> users = new ArrayList<RealtimeUser>();
	
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

	public RealtimeUser getUser() {
		return user;
	}

	public void setUser(RealtimeUser user) {
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

	public List<RealtimeUser> getUsers() {
		return users;
	}

	public void setUsers(List<RealtimeUser> users) {
		this.users = users;
	}

	public List<String> getInfos() {
		return infos;
	}

	public void setInfos(List<String> infos) {
		this.infos = infos;
	}
	
	
	

}
