package com.fajar.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

@Controller
public class MainController {
	Logger log = LoggerFactory.getLogger(MainController.class);
	static RestTemplate restTemplate = new RestTemplate();

	public MainController() {
		log.info("---------------------------GOTO WebSocket controller------------------------------");
	}

	@RequestMapping(value = { "/", "home" })
	public void index(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

//		if (userService.sessionUser(request))
//			response.sendRedirect("/puskesmas/user/login");
//		else
//			response.sendRedirect("/puskesmas/admin/home");
	}

	@RequestMapping(value = { "time" }, method = RequestMethod.POST)
	public void time(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/plain");
//		if (userService.sessionUser(request)) {
//			response.getWriter().write(new Date().toString());
//		}else {
//			response.getWriter().write("Invalid Request");
//		}

	}

	@RequestMapping(value = { "redirect/{protocol}/{link}/{domainExt}", "redirect/{protocol}/{link}/{domainExt}/{additionals}" }, method = RequestMethod.GET)
	public void goTo(@PathVariable(name = "protocol") String protocol, @PathVariable(name = "link") String link,
			@PathVariable(name = "domainExt") String domainExt ,  @PathVariable(name = "additionals", required = false) String additionals, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		 
		try {
			writeResponse(protocol, link, domainExt, additionals, request, response);
			response.setStatus(200);
		} catch (Exception e) {
			response.getWriter().write("ERROR BRO: " + e.getMessage());
		}

	}

	private void writeResponse(String protocol, String link, String domainExt, String additionals,  HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		String fullPath =protocol + "://" + link + "." + domainExt;
		String fullPathStringified =protocol + ":\\/\\/" + link + "." + domainExt;
		if(additionals != null) {
			fullPath+="/"+additionals;
		}
		System.out.println("redirecting to: " + fullPath);
		ResponseEntity<String> result = restTemplate.getForEntity(fullPath,
				String.class);
		String html = (result.getBody());
		String replacement = request.getContextPath()+"/redirect/"+protocol+"/"+link+"/"+domainExt;
		String replacementStringified = request.getContextPath()+"\\/redirect\\/"+protocol+"\\/"+link+"\\/"+domainExt;
		response.setContentType(result.getHeaders().getContentType().getType());
		html = html.replace(fullPath, replacement);
		html = html.replace(fullPathStringified, replacementStringified);
		response.getWriter().write(html);
	}
}
