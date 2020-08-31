package com.fajar.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fajar.dto.EntityRoles;
import com.fajar.parameter.EntityParameter;
import com.fajar.service.GameSettingService;
import com.fajar.service.RealtimeService;
import com.fajar.util.JSONUtil;

/**
 * 
 * @author fajar
 *
 */
@Controller
@RequestMapping("web")
public class GeneralController {

	Logger log = LoggerFactory.getLogger(GeneralController.class);
	@Autowired
	private RealtimeService realtimeService;
	@Autowired
	private GameSettingService gameSettingService;

	public GeneralController() {
		log.info("-----------------GENERAL CONTROLLER------------------");
	}

	@GetMapping(value = "notfound")
	public String halamanNotFound(Model model) throws IOException {
		model.addAttribute("pesan", "Halaman tidak ditemukan");
		return "error/notfound";
	}

	@GetMapping(value = "noaccess")
	public String halamanNotAccessable(Model model) throws IOException {
		model.addAttribute("pesan", "Halaman tidak dapat diakses");
		return "error/notfound";
	}

	/**
	 * Realtime
	 */
	@GetMapping(value = "test-chatv1")
	public String testChat(Model model) {
		return "websocket/chat";
	}

	@GetMapping(value = "test-chatv2")
	public String testChat2(Model model) {
		return "websocket/chat2";
	}

	@GetMapping(value = "canvasv1")
	public String canvasv1(Model model, HttpServletRequest request, HttpServletResponse response) {
		System.out.println("----------------REQUESTING GAME PAGE-----------------");
		model.addAttribute("winW", EntityParameter.WIN_W); 
		model.addAttribute("winH", EntityParameter.WIN_H); 
		model.addAttribute("baseHealth", EntityParameter.baseHealth); 
		
		model.addAttribute("rolePlayer", EntityRoles.ROLE_PLAYER); 
		model.addAttribute("roleBonusLife", EntityRoles.ROLE_BONUS_LIFE); 
		model.addAttribute("roleBonusArmor", EntityRoles.ROLE_BONUS_ARMOR); 
		model.addAttribute("roleRight", EntityRoles.ROLE_ROAD_RIGHT); 
		model.addAttribute("roleLeft", EntityRoles.ROLE_ROAD_LEFT); 
		model.addAttribute("roleUp", EntityRoles.ROLE_ROAD_UP); 
		model.addAttribute("roleDown", EntityRoles.ROLE_ROAD_DOWN);
		model.addAttribute("roleFinish", EntityRoles.ROLE_FINISH_LINE); 
		model.addAttribute("roleGeneralLayout", EntityRoles.ROLE_LAYOUT_1); 
		
		model.addAttribute("roles", JSONUtil.listToJson(EntityRoles.roles()));
		System.out.println(11);
		model.addAttribute("layouts", realtimeService.getJsonListOfLayouts());
		System.out.println(12);
		model.addAttribute("staticImages", JSONUtil.listToJson(EntityParameter.assets()));
		System.out.println(13);
		model.addAttribute("contextPath", request.getContextPath());
		model.addAttribute("serverNames", gameSettingService.getServerList());
		
		System.out.println("----------------END REQUESTING GAME PAGE-----------------");
		return "websocket/anim/canvas1";
	}
}
