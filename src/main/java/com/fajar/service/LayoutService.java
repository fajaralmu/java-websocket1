package com.fajar.service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fajar.dto.Entity;
import com.fajar.dto.Physical;
import com.fajar.parameter.EntityParameter;
import com.fajar.util.JSONUtil;

import lombok.AllArgsConstructor;

@Service 
@AllArgsConstructor 
public class LayoutService {
	Logger log = LoggerFactory.getLogger(LayoutService.class);

 
	private List<Entity> layouts = new ArrayList<>();
	 
	private  List<Integer> greenValues = new ArrayList<>();
	 
	private  List<Entity> stages = new ArrayList<>();
	 
	private  Map<Integer, List<Entity>> groupedStages = new HashMap<Integer, List<Entity>>();
	
	private Map<Integer, Integer> stagesRole = new HashMap<>();
	 
	private int startX=100;
	 
	private int startY=100;
	 
	public  int maxStage=0;
	 
	public  int minStage=0;


	private String jsonLayoutList;
	
	

	 

//	public static void main(String[] ddf) {
//		new LayoutService().load();
//		System.out.println(greenValues);
//		for(Integer key: groupedStages.keySet()) {
//			System.out.println("KEY:"+key+", val: "+groupedStages.get(key));
//		}
////		System.out.println(JSONUtil.listToJson(layouts));
////		System.out.println(JSONUtil.listToJson(stages));
//		for (Entity layout : layouts) {
//			System.out.println(layout);
//		}
//	}

	public LayoutService() {
		log.info("======================LAYOUT SERVICE======================");
	}
	
	public Entity getLayoutById(Integer id) {
		for (Entity layout : layouts) {
//			System.out.println("GET BY LAYOUT ID "+layout.getId()+" => " + (layout.getId()-(id)==0));
			if(layout.getId()-(id)==0) {
//				System.out.println("LAYOUT:=>"+layout);
				return layout;
			}
			
		}
		return null;
	}
	 

	public void load() {
		try {
			log.info("------------------WILL.... LOAD LAYOUT LAYOUT: {}", getClass().getCanonicalName());
			//String layoutStage = "file:/D:/Development/Assets/websocket/layout1-stage.png";
			String layoutStage = "https://lh3.googleusercontent.com/PI3nd8hGpDbyNBEhylXev6KFfBB0C5DK1Hy60ChvOhflPGwF2tA7iq_uSnUlIsPz5AIrRC-n1GVwuMKdKRyJUxyftpcbjVXyoWJFhEOmokYXMe6cZVKD0QSdAeYnwCqMWhJWdZBajz14UYLeekXKpzyyk1KCqZ8vDn3ruJJLpMVIxMJr2tSDyO3n5fiVH0dUdOc0BocJrA_qx-cxVIDTJW-dVhg22mctWTfXX4ChieoI3p5vguT2eKA77g22JDcRStMqwDrZzyCyonOz0kqTT4gAKQeFYRd5xLPBht-rUznuM0RpRxbcsEindEp4sF9kXO1SYXjOUi9DT9cgcmK1YYbqL0rrTxb-SIBuECqtgeJkrKh7r2pVDyMQu8pjo-6DaRtDQHAWgdemKU66iJCPmn_01wMkJp4s4Kbf0GQVJk8o1rPIkN1hfO7XEKLbCIu_YmSUsVy3_QLzZe4BKBayXLn7IoyUa0yxRmoYWVi27lQ6XYOvKwW-JJy-NO_0msSNrJJCqM5V6hJ3SL0c1SfCeI3_fPraEZt_hDdDqS43VlCOqCk6XwrJ4ck5K8j6hjSAk4K9O4rvf72qDB0AXkCkDHn1nTO8bLuWE6FtWEbjMCpw3eDt3K43EO1ueeCTJXvA-ESDrOfbiTXM3_fP5SSbfsnibm_r4pjEI7tD1Q5gsxmqaKdpa1bsFw=w120-h60-no";
			
			//String layoutBg = "file:/D:/Development/Assets/websocket/layout1.png";
			String layoutBg= "https://lh3.googleusercontent.com/Bh8hSWIpPcTSeefllvODW5CCvFasFsinFJQshjuPdajkY4Ip2-vGMs2h4UJ5etyVeZihWPq9xLoN_YWw2li5v-r48y1IE3ZngdmOLNDacwkdTJdEa397jZhWZsuWxDykvm-cwTJUmDw3mrwfHadH_K7hhaCMmjXPE52k24ShRrUcd_7mC85c4ZnpOz4Xf5vLZKU28GCW4diGG3YisCClucRfW8xEn8n1JJUztxeidZlgQ6LdetyQG_s6SeVZeqR_u1Sh2dRXM4xgcJz4OdzuYeLyhyvw7y-op1q6zxcZLtANC4nbkn8DgzlIzirAPxEeDBTlESQdLObN7SbStszXDrPJgKweR9BAobbjNZirH1jZrxUdyHa5tnz-3wOW976SjEVrGF6xhjRG4hQi7e_bUaPxpdTB0dmwLwobQddZXxmuj7jtQpVJdU7uyRAd26IC3F9dW1HLKFTXcCVGlnpWQ7gpA1txQpVkCJcN4-_JA9iLVeA016ccn8_witYD8Pm7HCyjsUBbVSOLZS8cGSLuuUQQOFfl5heY-A5v0n81EhFFD7oOZrrPLM8Vp_BbJc8l77IZRN2W0pU9KL4WkI9F1E8IZMyG7dyog3znAyUhi_QUztIin_VwCo3ehHcDldgGKrCI5vWTaQkeypZDaacLlqF-WeDW4UbNjjJjWqE1c1QsvOYPqe-QvQ=w120-h60-no";			URL path = new URL(layoutBg);
			URL pathStage = new URL(layoutStage);
			log.info("------------------IMAGE PATH1: {}, {}", path, pathStage);
			log.info("------------------IMAGE PATH2: {}, {}", path.getPath(), pathStage.getPath());
			log.info("------------------IMAGE PATH3: {}, {}", path.getFile(), pathStage.getFile());
			BufferedImage layout1 = ImageIO.read(path);
			BufferedImage layout1Stage = ImageIO.read(pathStage);
			loadStage(layout1Stage);
			createLayout(layout1);
			System.out.println("==============CREATING JSON LAYOUT===========");
			this.jsonLayoutList = JSONUtil.listToJson(this.layouts);
//			for(Entity layout:layouts) {
//				System.out.println("LAYOUT ITEM: "+layout);
//			}
			System.out.println("LAYOUT COUNT: "+layouts.size());
			System.out.println("LAYOUT STAGES: "+this.stagesRole);
			System.out.println("JSON LIST: "+jsonLayoutList);
			System.out.println("**************LAYOUT LOADED************");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private boolean isStored(int greenValue) {
		for (Integer integer : greenValues) {
			if(integer.equals(greenValue))
				return true;
		}
		greenValues.add(greenValue);
		return false;
	}
	
	private void loadStage(BufferedImage denah) {
		int width = denah.getWidth();
		int height = denah.getHeight();
		int currentGreen = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				System.out.print("*");
				int pixel = denah.getRGB(x, y);
				int red = (pixel >> 16) & 0xff;
				int green = (pixel >> 8) & 0xff;
				int blue = (pixel) & 0xff;

				if (red == 123 ) {
					int xPos = x * 10  ;
					int yPos = y * 10;
					Entity layoutEntity = new Entity(new Random().nextInt(100100100) + 1,
							String.valueOf(green), new Date());
					Physical entity = new Physical();
					entity.setX(xPos);
					entity.setLayout(true);
					entity.setY(yPos);
					entity.setW(10);
					entity.setH(10);
					entity.setRole(EntityParameter.ROLE_STAGE);
					layoutEntity.setPhysical(entity);
					stages.add(layoutEntity);
					isStored(green);
				}
			}
		}
		System.out.println();
		groupStages();
		setMinAndMaxStage();
	}
	
	private void setMinAndMaxStage() {
		int max = 0;
		for(Integer key:groupedStages.keySet()) {
			if(key>=max) {
				max = key;
			}
		}
		this.maxStage = max;
		int min = max;
		for(Integer key:groupedStages.keySet()) {
			if(key<=min) {
				min = key;
			}
		}
		this.minStage = min;
	}
	
	private void groupStages() {
		groupedStages.clear();
		for (Entity stage : stages) {
			for (Integer integer : greenValues) {
				if(stage.getName().equals(integer.toString())) {
					if(groupedStages.get(integer) == null) {
						List<Entity> groupedStageList = new ArrayList<>();
						groupedStageList.add(stage);
						groupedStages.put(integer, groupedStageList);
					}else {
						groupedStages.get(integer).add(stage);
					}
				}
			}
		}
	}
	
	public int getStagesCount() {
		return this.groupedStages.keySet().size();
	}
	
	public int getLayoutRole(int stageId) {
		for(Integer key:groupedStages.keySet()) {
			if(key.equals(stageId)) {
				return groupedStages.get(key).get(0).getPhysical().getRole();
			}
		}
		return 0;
	}

	public void createLayout(BufferedImage denah) {

		int width = denah.getWidth();
		int height = denah.getHeight();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				System.out.print("*");
				int pixel = denah.getRGB(x, y);
				int red = (pixel >> 16) & 0xff;
				int green = (pixel >> 8) & 0xff;
				int blue = (pixel) & 0xff;

				if (red == 120 && green == 120 && blue == 120) {
					int xPos = x * 10 ;
					int yPos = y * 10;
					this.startX=(xPos);
					this.startY=(yPos);
				}
				if (red == 255 && green == 0 && blue == 0) {
					int xPos = x * 10 ;
					int yPos = y * 10;
					Entity layoutEntity = new Entity(new Random().nextInt(100100100) + 1,
							"layout_" + xPos + "-" + yPos, new Date());
					Physical entity = new Physical();
					entity.setX(xPos);
					entity.setY(yPos);
					entity.setLayout(true);
				
					entity.setRole(EntityParameter.ROLE_LAYOUT_1);
					layoutEntity.setPhysical(entity);
					layouts.add(layoutEntity);
				}
				if (red == 0 && green == 0 && blue == 0) {
					int xPos = x * 10  ;
					int yPos = y * 10;
					Entity layoutEntity = new Entity(new Random().nextInt(100100100) + 1,
							"layout_" + xPos + "-" + yPos, new Date());
					Physical entity = new Physical();
					entity.setX(xPos);
					entity.setLayout(true);
					entity.setY(yPos);
					entity.setW(10);
					entity.setH(10);
					entity.setRole(EntityParameter.ROLE_LAYOUT_1);
					layoutEntity.setPhysical(entity);
					layouts.add(layoutEntity);
				}
				
				/**
				 * CIRCUIT AND STAGE
				 */
				if (red == 0 && green == 255 && blue == 0) {
					int xPos = x * 10  ;
					int yPos = y * 10;
					Entity layoutEntity = new Entity(new Random().nextInt(100100100) + 1,
							"road_" + xPos + "-" + yPos, new Date());
					Physical entity = new Physical();
					entity.setX(xPos);
					entity.setLayout(true);
					entity.setY(yPos);
					entity.setW(10);
					entity.setH(10);
					entity.setRole(EntityParameter.ROLE_ROAD_LEFT);
					layoutEntity.setPhysical(entity);
					layouts.add(getStage(layoutEntity,EntityParameter.ROLE_ROAD_LEFT));
				}
				if (red == 0 && green == 0 && blue == 255) {
					int xPos = x * 10  ;
					int yPos = y * 10;
					Entity layoutEntity = new Entity(new Random().nextInt(100100100) + 1,
							"road_" + xPos + "-" + yPos, new Date());
					Physical entity = new Physical();
					entity.setX(xPos);
					entity.setLayout(true);
					entity.setY(yPos);
					entity.setW(10);
					entity.setH(10);
					entity.setRole(EntityParameter.ROLE_ROAD_RIGHT);
					layoutEntity.setPhysical(entity);
					layouts.add(getStage(layoutEntity,EntityParameter.ROLE_ROAD_RIGHT));
				}
				if (red == 255 && green == 0 && blue == 100) {
					int xPos = x * 10  ;
					int yPos = y * 10;
					Entity layoutEntity = new Entity(new Random().nextInt(100100100) + 1,
							"road_" + xPos + "-" + yPos, new Date());
					Physical entity = new Physical();
					entity.setX(xPos);
					entity.setLayout(true);
					entity.setY(yPos);
					entity.setW(10);
					entity.setH(10);
					entity.setRole(EntityParameter.ROLE_ROAD_UP);
					layoutEntity.setPhysical(entity);
					layouts.add(getStage(layoutEntity,EntityParameter.ROLE_ROAD_UP));
				}
				if (red == 111 && green == 111 && blue == 111) {
					int xPos = x * 10  ;
					int yPos = y * 10;
					Entity layoutEntity = new Entity(new Random().nextInt(100100100) + 1,
							"road_finish_" + xPos + "-" + yPos, new Date());
					Physical entity = new Physical();
					entity.setX(xPos);
					entity.setLayout(true);
					entity.setY(yPos);
					entity.setW(10);
					entity.setH(10);
					entity.setRole(EntityParameter.ROLE_FINISH_LINE);
					layoutEntity.setPhysical(entity);
					layouts.add(getStage(layoutEntity,EntityParameter.ROLE_FINISH_LINE));
				}
				if (red == 255 && green == 100 && blue == 0) {
					int xPos = x * 10  ;
					int yPos = y * 10;
					Entity layoutEntity = new Entity(new Random().nextInt(100100100) + 1,
							"road_" + xPos + "-" + yPos, new Date());
					Physical entity = new Physical();
					entity.setX(xPos);
					entity.setLayout(true);
					entity.setY(yPos);
					entity.setW(10);
					entity.setH(10);
					entity.setRole(EntityParameter.ROLE_ROAD_DOWN);
					layoutEntity.setPhysical(entity);
					layouts.add(getStage(layoutEntity,EntityParameter.ROLE_ROAD_DOWN));
				}
			}
		}
		System.out.println();

	}
	
	public int getStageRole(int stageId) {
		for(Integer key:stagesRole.keySet()) {
			if(key.equals(stageId))
				return stagesRole.get(key);
		}
		return EntityParameter.ROLE_ROAD_DOWN;
	}
	
	private Entity getStage(Entity layout,final int role) {
		Physical layoutPhysical = layout.getPhysical();
		
		for(Integer key:groupedStages.keySet()) {
			for (Entity layoutStage : groupedStages.get(key)) {
				layoutStage.getPhysical().setRole(layoutPhysical.getRole());
				
				 
				Physical layoutStagePhysical = layoutStage.getPhysical();
				if(layoutPhysical.getX().equals(layoutStagePhysical.getX())
						&& layoutPhysical.getY().equals(layoutStagePhysical.getY())) {
					layout.setStageId(key);
					stagesRole.put(key, role);
				}
			}
		}
		System.out.println("CEK "+role+"->"+layout);
		System.out.println("MAP STAGES:"+stagesRole);
		return layout;
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
