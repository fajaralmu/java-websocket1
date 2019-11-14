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

import com.fajar.parameter.EntityParameter;
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
		System.out.println(1);
		model.addAttribute("winH", EntityParameter.WIN_H);
		System.out.println(2);
		model.addAttribute("baseHealth", EntityParameter.baseHealth);
		System.out.println(3);
		model.addAttribute("rolePlayer", EntityParameter.ROLE_PLAYER);
		System.out.println(4);
		model.addAttribute("roleBonusLife", EntityParameter.ROLE_BONUS_LIFE);
		System.out.println(5);
		model.addAttribute("roleBonusArmor", EntityParameter.ROLE_BONUS_ARMOR);
		System.out.println(6);
		model.addAttribute("roleRight", EntityParameter.ROLE_ROAD_RIGHT);
		System.out.println(7);
		model.addAttribute("roleLeft", EntityParameter.ROLE_ROAD_LEFT);
		System.out.println(8);
		model.addAttribute("roleUp", EntityParameter.ROLE_ROAD_UP);
		System.out.println(9);
		model.addAttribute("roleDown", EntityParameter.ROLE_ROAD_DOWN);
		System.out.println(10);
		model.addAttribute("roles", JSONUtil.listToJson(EntityParameter.roles()));
		System.out.println(11);
		model.addAttribute("layouts", realtimeService.getJsonListOfLayouts());
		System.out.println(12);
		model.addAttribute("staticImages", JSONUtil.listToJson(EntityParameter.assets()));
		System.out.println(13);
		model.addAttribute("contextPath", request.getContextPath());
		System.out.println("----------------END REQUESTING GAME PAGE-----------------");
		return "websocket/anim/canvas1";
	}
}
