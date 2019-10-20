package com.fajar.dto;

import java.io.Serializable;

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
public class Missile implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Physical physical;
	private Integer id;
	private Integer entityId;
	 

}
