package com.fajar.service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.fajar.dto.Entity;
import com.fajar.dto.Physical;
import com.fajar.parameter.EntityParameter;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class LayoutService {

	private List<Entity> layouts = new ArrayList<>();
	private List<Integer> greenValues = new ArrayList<>();
	private List<Entity> stages = new ArrayList<>();

	private Map<Integer, List<Entity>> groupedStages = new HashMap<Integer, List<Entity>>();
	private Map<Integer, Integer> stagesRole = new HashMap<>();

	private int startX = 100;
	private int startY = 100;
	private int maxStage = 0;
	private int minStage = 0;
	private int layoutItemHeight = 10;
	private int layoutItemWidth = 10;

	@Value("resources/config/layout1-flow-resized.png")
	private Resource layoutBgResource;
	@Value("resources/config/layout1-stage.png")
	private Resource layoutStageResource;

	private String jsonLayoutList;
	private ObjectMapper objectMapper = new ObjectMapper();
	private Random random = new Random();

	public LayoutService() {
		System.out.println("======================LAYOUT SERVICE======================");
	}

	public Entity getLayoutById(Integer id) {
		for (Entity layout : layouts) {
//			System.out.println("GET BY LAYOUT ID "+layout.getId()+" => " + (layout.getId()-(id)==0));
			if (layout.getId() - (id) == 0) {
//				System.out.println("LAYOUT:=>"+layout);
				return layout;
			}

		}
		return null;
	}

	public void load() {
		try {
			System.out.println("------------------WILL.... LOAD LAYOUT LAYOUT: " + getClass().getCanonicalName());

//			Resource layoutBgResource = new ClassPathResource("com/fajar/assets/layout1-flow-resized.png");
//			Resource layoutStageResource = new ClassPathResource("com/fajar/assets/layout1-stage.png");

			// String layoutBg = "file:/D:/Development/Assets/websocket/layout1.png";
//			String layoutStage = "http://developmentmode.000webhostapp.com/assets/duckrace/layout1-stage.png";
//			String layoutBg= "http://developmentmode.000webhostapp.com/assets/duckrace/layout1.png";			
			// String layoutStage =
			// "file:/D:/Development/Assets/websocket/layout1-stage.png";

//			URL path = new URL(layoutBg);
//			URL pathStage = new URL(layoutStage);

			System.out.println("------------------BACKGROUND PATH1: " + layoutBgResource.getFile().getCanonicalFile());
			System.out.println("------------------STAGE PATH2: " + layoutStageResource.getFile().getCanonicalFile());

			BufferedImage layout1 = ImageIO.read(layoutBgResource.getFile());
			BufferedImage layout1Stage = ImageIO.read(layoutStageResource.getFile());
			loadStage(layout1Stage);
			createLayout(layout1);

			System.out.println("LAYOUT COUNT: " + layouts.size());

			System.out.println("==============CREATING JSON LAYOUT===========");
			this.jsonLayoutList = objectMapper.writeValueAsString(layouts);// JSONUtil.listToJson(this.layouts);

			System.out.println("LAYOUT STAGES: " + this.stagesRole);
//			System.out.println("JSON LIST: " + jsonLayoutList);
			System.out.println("**************LAYOUT LOADED************");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean updateGreenValue(int greenValue) {
		for (Integer integer : greenValues) {
			if (integer.equals(greenValue))
				return true;
		}
		greenValues.add(greenValue);
		return false;
	}

	/**
	 * set user stages
	 * 
	 * @param stageLayout
	 */
	private void loadStage(BufferedImage stageLayout) {

		int width = stageLayout.getWidth();
		int height = stageLayout.getHeight();
		System.out.println("loadStage size:" + width + "x" + height);
//		int currentGreen = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				System.out.print("*");
				int pixel = stageLayout.getRGB(x, y);
				int red = (pixel >> 16) & 0xff;
				int green = (pixel >> 8) & 0xff;
//				int blue = (pixel) & 0xff;

				if (red == 123) {
					int xPos = x * 10;
					int yPos = y * 10;
					Entity layoutEntity = new Entity(new Random().nextInt(100100100) + 1, String.valueOf(green),
							new Date());
					Physical entity = new Physical();
					entity.setX(xPos);
					entity.setLayout(true);
					entity.setY(yPos);
					entity.setW(layoutItemWidth);
					entity.setH(layoutItemHeight);
					entity.setRole(EntityParameter.ROLE_STAGE);
					layoutEntity.setPhysical(entity);
					stages.add(layoutEntity);
					updateGreenValue(green);
				}
			}
		}
		System.out.println();
		System.out.println("LOADED STAGE: " + stages.size());
		groupStages();
		setMinAndMaxStage();
	}

	/**
	 * get minimum and maximum user position in the road race
	 */
	private void setMinAndMaxStage() {
		int max = 0;
		for (Integer key : groupedStages.keySet()) {
			if (key >= max) {
				max = key;
			}
		}
		this.maxStage = max;
		int min = max;
		for (Integer key : groupedStages.keySet()) {
			if (key <= min) {
				min = key;
			}
		}
		this.minStage = min;

		System.out.println("min stage: " + minStage + "max stage: " + maxStage);
	}

	private void groupStages() {
		System.out.println("Grouping Stages");
		groupedStages.clear();
		for (Entity stage : stages) {
			for (Integer greenValue : greenValues) {
				if (stage.getName().equals(greenValue.toString())) {
					if (groupedStages.get(greenValue) == null) {
						List<Entity> groupedStageList = new ArrayList<>();
						groupedStageList.add(stage);
						groupedStages.put(greenValue, groupedStageList);
					} else {
						groupedStages.get(greenValue).add(stage);
					}
				}
			}
		}
		System.out.println("End Grouping Stages: " + groupedStages.size());
	}

	public int getStagesCount() {
		return this.groupedStages.keySet().size();
	}

	public int getLayoutRole(int stageId) {
		for (Integer key : groupedStages.keySet()) {
			if (key.equals(stageId)) {
				return groupedStages.get(key).get(0).getPhysical().getRole();
			}
		}
		return 0;
	}
	
	private int randomId() {
		return random.nextInt(100100100) + 1;
	}

	/**
	 * create layout including user default position and road directions
	 * 
	 * @param layout
	 */
	public void createLayout(BufferedImage layout) {

		int width = layout.getWidth();
		int height = layout.getHeight();

		System.out.println("Creating layout, startX: " + startX + ", startY: " + startY);
		System.out.println("Layout size: " + width + "x" + height);
		int multiplier = 10;
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				System.out.print("*");
				int pixel = layout.getRGB(x, y);
				int red = (pixel >> 16) & 0xff;
				int green = (pixel >> 8) & 0xff;
				int blue = (pixel) & 0xff;
				int xPos = x * multiplier;
				int yPos = y * multiplier;

				if (red == 120 && green == 120 && blue == 120) {
					 
					this.startX = (xPos);
					this.startY = (yPos);
				}
				if (red == 255 && green == 0 && blue == 0) {
					 
					Entity layoutEntity = new Entity(randomId(), "layout_" + xPos + "-" + yPos,
							new Date());
					Physical physicalInformation = new Physical();
					physicalInformation.setX(xPos);
					physicalInformation.setY(yPos);
					physicalInformation.setLayout(true); 
					physicalInformation.setRole(EntityParameter.ROLE_LAYOUT_1);
					
					layoutEntity.setPhysical(physicalInformation);
					layouts.add(layoutEntity);
				}
				if (red == 0 && green == 0 && blue == 0) {
					 
					Entity layoutEntity = new Entity(randomId(), "layout_" + xPos + "-" + yPos );
					
					Physical physicalInformation = commonPhysicalInformation(EntityParameter.ROLE_LAYOUT_1, xPos, yPos);
					layoutEntity.setPhysical(physicalInformation);
					layouts.add(layoutEntity);
				}

				/**
				 * CIRCUIT DIRECTIONS
				 */
				if (red == 0 && green == 255 && blue == 0) {
					 
					Entity layoutEntity = new Entity(randomId(), "road_" + xPos + "-" + yPos );
					
					Physical physicalInformation = commonPhysicalInformation(EntityParameter.ROLE_ROAD_LEFT, xPos, yPos);
					layoutEntity.setPhysical(physicalInformation);
					layouts.add(getStage(layoutEntity, EntityParameter.ROLE_ROAD_LEFT));
				}
				if (red == 0 && green == 0 && blue == 255) {
					 
					Entity layoutEntity = new Entity(randomId(), "road_" + xPos + "-" + yPos );
					
					Physical physicalInformation = commonPhysicalInformation(EntityParameter.ROLE_ROAD_RIGHT, xPos, yPos);
					layoutEntity.setPhysical(physicalInformation);
					layouts.add(getStage(layoutEntity, EntityParameter.ROLE_ROAD_RIGHT));
				}
				if (red == 255 && green == 0 && blue == 100) {
				
					Entity layoutEntity = new Entity(randomId(), "road_" + xPos + "-" + yPos );
					
					Physical physicalInformation = commonPhysicalInformation(EntityParameter.ROLE_ROAD_UP, xPos, yPos);
					layoutEntity.setPhysical(physicalInformation);
					layouts.add(getStage(layoutEntity, EntityParameter.ROLE_ROAD_UP));
				}
				if (red == 111 && green == 111 && blue == 111) {
					 
					Entity layoutEntity = new Entity(randomId(), "road_finish_" + xPos + "-" + yPos );
					
					Physical physicalInformation = commonPhysicalInformation(EntityParameter.ROLE_FINISH_LINE, xPos, yPos); 
					layoutEntity.setPhysical(physicalInformation);
					layouts.add(getStage(layoutEntity, EntityParameter.ROLE_FINISH_LINE));
				}
				if (red == 255 && green == 100 && blue == 0) {
				 
					Entity layoutEntity = new Entity(randomId(), "road_" + xPos + "-" + yPos );
					
					Physical physicalInformation = commonPhysicalInformation(EntityParameter.ROLE_ROAD_DOWN, xPos, yPos); 
					layoutEntity.setPhysical(physicalInformation);
					layouts.add(getStage(layoutEntity, EntityParameter.ROLE_ROAD_DOWN));
				}
			}
		}
		System.out.println();
		System.out.println("END CREATING lAYOUT");
		System.out.println("Created layout, startX: " + startX + ", startY: " + startY);

	}

	private Physical commonPhysicalInformation(Integer role, int xPos, int yPos) {
		Physical entity = new Physical();
		entity.setX(xPos);
		entity.setLayout(true);
		entity.setY(yPos);
		entity.setW(10);
		entity.setH(10);
		entity.setRole(role);
		return entity;
	}

	public int getStageRole(int stageId) {
		for (Integer key : stagesRole.keySet()) {
			if (key.equals(stageId))
				return stagesRole.get(key);
		}
		return EntityParameter.ROLE_ROAD_DOWN;
	}

	private Entity getStage(Entity entity, final int role) {
		Physical layoutPhysical = entity.getPhysical();

		for (Integer key : groupedStages.keySet()) {
			for (Entity layoutStage : groupedStages.get(key)) {
				layoutStage.getPhysical().setRole(layoutPhysical.getRole());

				Physical layoutStagePhysical = layoutStage.getPhysical();
				if (layoutPhysical.getX().equals(layoutStagePhysical.getX())
						&& layoutPhysical.getY().equals(layoutStagePhysical.getY())) {
					entity.setStageId(key);
					stagesRole.put(key, role);
				}
			}
		}
//		System.out.println(":CEK ROLE " + role + "Entity=====>" + entity);
//		System.out.println(":MAP STAGES>>" + stagesRole);
		return entity;
	}

	public List<Entity> getLayouts() {
		return layouts;
	}

	public Integer getMinStage() {
		return this.minStage;
	}

	public Integer getStartX() {
		return this.startX;
	}

	public Integer getStartY() {
		return this.startY;
	}

	public String getJsonListOfLayouts() {
		return this.jsonLayoutList;
	}

}
