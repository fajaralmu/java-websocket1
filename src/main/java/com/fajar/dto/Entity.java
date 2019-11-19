package com.fajar.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fajar.annotation.Dto;
import com.fajar.parameter.EntityParameter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * game entity ; player, layout, etc
 * @author Republic Of Gamers
 *
 */
@Dto
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Entity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6053163038967434721L;
	private Integer id;
	private String name;
	@Builder.Default
	private Date joinedDate = new Date();
	private Physical physical;
	@Builder.Default
	@Getter(value = AccessLevel.NONE)
	private List<Integer> stagesPassed = new ArrayList<>();
	@Builder.Default
	private int life = EntityParameter.baseHealth;
	private boolean active;
	//RACE
	private int layoutId;
	private int stageId;
	private int position;
	private int lap;
	
	@Builder.Default
	@Getter(value = AccessLevel.NONE)
	private List<Missile> missiles = new ArrayList<Missile>();

	public Entity(Integer id, String name, Date createdDate) {
		this.id = id;
		this.name = name;
		this.joinedDate = createdDate;
		this.missiles = new ArrayList<>();
		life = EntityParameter.baseHealth;
	}
	
	public List<Missile> getMissiles() {
		if(this.missiles == null) {
			return new ArrayList<>();
		}
		return this.missiles;
	}
	
	public void setStage() {
		
		for (int i = 0; i < stagesPassed.size(); i++) {
			int array_element =stagesPassed.get(i);
			if(array_element == 0) {
				stagesPassed.remove(i);
			}
		}
		 
		boolean stageIsInclude = false;
		for(int stage:getStagesPassed()) {
			if(stage == stageId) {
				stageIsInclude = true;
				break;
			}
		}
		if(stageIsInclude==false) {
			this.stagesPassed.add(stageId);
		}
	}
	
	public List<Integer> getStagesPassed(){
		if(this.stagesPassed == null) {
			this.stagesPassed = new ArrayList<>();
		}
		return this.stagesPassed;
	}

}
