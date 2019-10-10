package com.fajar.dto;

import java.io.Serializable;

import com.fajar.annotation.Dto;
import com.fajar.parameter.EntityParameter;

@Dto
public class Entity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8774779913578007103L;
	private Integer x;
	private Integer y;
	private String color;
	private Integer w = 63;
	private Integer h = 63;
	private String direction = "r";
	private Integer role = EntityParameter.ROLE_PLAYER;
	private Long period;
	
	
	public Long getPeriod() {
		return period;
	}
	public void setPeriod(Long period) {
		this.period = period;
	}
	public Integer getRole() {
		return role;
	}
	public void setRole(Integer role) {
		this.role = role;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public Integer getW() {
		return w;
	}
	public void setW(Integer w) {
		this.w = w;
	}
	public Integer getH() {
		return h;
	}
	public void setH(Integer h) {
		this.h = h;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public Integer getX() {
		return x;
	}
	public void setX(Integer x) {
		this.x = x;
	}
	public Integer getY() {
		return y;
	}
	public void setY(Integer y) {
		this.y = y;
	}
	@Override
	public String toString() {
		return "Entity [x=" + x + ", y=" + y + ", color=" + color + "]";
	}
	
	
	
}
