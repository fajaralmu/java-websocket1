package com.fajar.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fajar.annotation.Dto;
import com.fajar.parameter.EntityParameter;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
public class Entity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6053163038967434721L;
	private final Integer id;
	private final String name;
	@Builder.Default
	private Date joinedDate = new Date();
	@Builder.Default
	@Getter(value = AccessLevel.NONE)
	private List<Integer> stagesPassed = new ArrayList<>();
	@Builder.Default
	private int life = EntityParameter.baseHealth;
	
	private boolean active;
	private boolean forceUpdate; //force update entity position based on  what is calculated in server side although the server lastUpdated < client lastUpdated
	
	private Physical physical;
	
	//RACE
	private int layoutId;
	private int stageId;
	private int position;
	private int lap;
	
	@Builder.Default
	@Getter(value = AccessLevel.NONE)
	private List<Missile> missiles = new ArrayList<Missile>();
	
	//////////////FOR PROCESSING ONLY//////////////
	@JsonIgnore
	private boolean continueLoop;
	@JsonIgnore
	private boolean breakLoop;

	public Entity(Integer id, String name, Date createdDate) {
		this.id = id;
		this.name = name;
		this.joinedDate = createdDate;
		this.missiles = new ArrayList<>();
		life = EntityParameter.baseHealth;
	}
	public Entity(Integer id, String name ) {
		this.id = id;
		this.name = name;
		this.joinedDate = new Date();
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
		try {
			for (int i = 0; i < stagesPassed.size(); i++) {
				int array_element = stagesPassed.get(i);
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
		}catch (Exception e) {
			 
		}
	}
	
	public List<Integer> getStagesPassed(){
		if(this.stagesPassed == null) {
			this.stagesPassed = new ArrayList<>();
		}
		return this.stagesPassed;
	}

}
