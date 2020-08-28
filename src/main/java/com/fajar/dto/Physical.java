package com.fajar.dto;

import java.io.Serializable;
import java.util.Date;

import com.fajar.annotation.Dto;
import com.fajar.parameter.EntityRoles;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Dto
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Physical implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8774779913578007103L;
	private Integer x;
	private Integer y;
	@Builder.Default
	private String color = "rgb(0,0,0)";
	@Builder.Default
	private Integer w = 30;
	@Builder.Default
	private Integer h = 30;
	@Builder.Default
	private String direction = "r";
	private Boolean layout;
	@Builder.Default
	private EntityRoles role = EntityRoles.ROLE_PLAYER;
	private Long period;
	private Date lastUpdated;
	

	
	
	public Date getLastUpdated() {
		return lastUpdated;
	}
	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	public Boolean isLayout() {
		return layout;
	}
	public void setLayout(Boolean layout) {
		this.layout = layout;
	}
	public Long getPeriod() {
		return period;
	}
	public void setPeriod(Long period) {
		this.period = period;
	}
	public EntityRoles getRole() {
		return role;
	}
	public void setRole(EntityRoles role) {
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
		return "Entity [x=" + x + ", y=" + y + ", color=" + color + ", w=" + w + ", h=" + h + ", direction=" + direction
				+ ", role=" + role + ", period=" + period + "]";
	}
	
	public static Boolean intersect(Entity mainuser,Entity user) {
		Physical mainPos = mainuser.getPhysical();
		Physical userPos = user.getPhysical();
		Integer mainX = mainPos.x;
		Integer mainY = mainPos.y;
		Integer mainW = mainPos.w;
		Integer mainH = mainPos.h;
		Integer userX = userPos.x;
		Integer userY = userPos.y;
		Integer userW = userPos.w;
		Integer userH = userPos.h;
		// console.log("MAIN",mainPos);
		// console.log("USER",userPos);
		boolean cond1 = false;
		boolean cond2 = false;
		boolean cond3 = false;
		boolean cond4 = false;

		if (userX >= mainX && mainX + mainW >= userX) {
			// console.log("1");
			if (userY >= mainY && mainY + mainH >= userY) {
				// console.log("2");
				cond1 = true;
				 
			}
		}
		if (mainX >= userX && userX + userW >= mainX) {
			// console.log("3");
			if (mainY >= userY && userY + userH >= mainY) {
				// console.log("-----4");
				cond2 = true;
				 
			}
		}
		if (mainX <= userX && mainX + mainW >= userX) {
			if (mainY >= userY && userY + userH >= mainY) {
				cond3 = true;
			 
			}
		}
		if (userX <= mainX && userX + userW >= mainX) {
			if (userY >= mainY && mainY + mainH >= userY) {
				cond4 = true;
				 
			}
		}
		if (cond1 || cond2 || cond3 || cond4) {
			return true;
		}

		return false;
	}

	 
	
	
	
}
