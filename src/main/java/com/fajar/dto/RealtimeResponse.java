package com.fajar.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fajar.annotation.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RealtimeResponse implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4025058368937979008L;
	private OutputMessage message;
	private Entity entity;
	private String responseCode;
	private String responseMessage;
	private Map<String, Object> info;
	@Builder.Default
	private List<Entity> entities = new ArrayList<Entity>();
	@Builder.Default
	private List<String> infos = new ArrayList<>();
	private List<String> availableServers;
	private String serverName;

	 
	public RealtimeResponse(String rc,String rm) {
		this.responseCode = rc;
		this.responseMessage = rm;
	}
	
	
	
	
	

}
