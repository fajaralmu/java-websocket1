package com.fajar.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fajar.annotation.Dto;
import com.fajar.dto.RealtimeResponse.RealtimeResponseBuilder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Dto
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RealtimeRequest implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6891178168583718796L;
	private Entity entity;
	
	
}
