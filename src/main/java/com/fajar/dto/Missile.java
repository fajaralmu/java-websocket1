package com.fajar.dto;

import java.io.Serializable;

import com.fajar.annotation.Dto;

@Dto
public class Missile implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Entity entity;
	private Integer id;
	private Integer userId;
	public Entity getEntity() {
		return entity;
	}
	public void setEntity(Entity entity) {
		this.entity = entity;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	
	

}
