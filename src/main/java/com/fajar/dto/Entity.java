package com.fajar.dto;

import java.io.Serializable;

import com.fajar.annotation.Dto;

@Dto
public class Entity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8774779913578007103L;
	private Integer x;
	private Integer y;
	private String color;
	private Integer w = 50;
	private Integer h = 50;
	private String direction = "r";
	
	
	
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
