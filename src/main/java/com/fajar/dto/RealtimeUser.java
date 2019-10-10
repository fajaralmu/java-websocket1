package com.fajar.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fajar.annotation.Dto;
import com.fajar.parameter.EntityParameter;

@Dto
public class RealtimeUser implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6053163038967434721L;
	private Integer id;
	private String name;
	private Date joinedDate;
	private Entity entity;
	private Integer life =EntityParameter.baseHealth;
	private boolean active;
	private List<Missile>  missiles = new ArrayList<>();
	
	   
	
	
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public Integer getLife() {
		return life;
	}
	public void setLife(Integer life) {
		this.life = life;
	}
	public List<Missile> getMissiles() {
		return missiles;
	}
	public void setMissiles(List<Missile> missiles) {
		this.missiles = missiles;
	}
	public Entity getEntity() {
		return entity;
	}
	public void setEntity(Entity entity) {
		this.entity = entity;
	}
	public RealtimeUser() {
		
	}
	public RealtimeUser(Integer id, String name, Date joinedDate) {
		super();
		this.id = id;
		this.name = name;
		this.joinedDate = joinedDate;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getJoinedDate() {
		return joinedDate;
	}
	public void setJoinedDate(Date joinedDate) {
		this.joinedDate = joinedDate;
	}
	@Override
	public String toString() {
		return "RealtimeUser [id=" + id + ", name=" + name + ", joinedDate=" + joinedDate + "]";
	}
	
	
	

}
