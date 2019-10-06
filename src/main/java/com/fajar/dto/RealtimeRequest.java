package com.fajar.dto;

import java.io.Serializable;

public class RealtimeRequest implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6891178168583718796L;
	private RealtimeUser user;
	
	public RealtimeUser getUser() {
		return user;
	}
	
	

	public void setUser(RealtimeUser user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "ChatRequest [user=" + user + "]";
	}
	
	
	
	
}
