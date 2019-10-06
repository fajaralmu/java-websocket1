package com.fajar.controller;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class MainController {
	Logger log = LoggerFactory.getLogger(MainController.class);
	
	public MainController(){
		log.info("---------------------------GOTO puskesmas controller------------------------------");
	}

	@RequestMapping(value = { "/", "home" })
	public void index(Model model, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		
//		if (userService.sessionUser(request))
//			response.sendRedirect("/puskesmas/user/login");
//		else
//			response.sendRedirect("/puskesmas/admin/home");
	}
	
	@RequestMapping(value = { "time" },method = RequestMethod.POST)
	public void time(Model model, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		response.setContentType("text/plain");
//		if (userService.sessionUser(request)) {
//			response.getWriter().write(new Date().toString());
//		}else {
//			response.getWriter().write("Invalid Request");
//		}
		
	}
}
