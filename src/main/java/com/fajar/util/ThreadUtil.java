package com.fajar.util;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadUtil {

	public static Thread run(Runnable runnable) {
		
		Thread thread  = new Thread(runnable);
		log.info("running thread: {}", thread.getId());
		log.info("active thread: {}", Thread.activeCount());
		thread.start(); 
		return thread;
	}
	
	public static void main(String[] args) {
		List<String> strings = new ArrayList<String>();
		
		strings.add("a");
		strings.add("b");
		strings.add("c");
		strings.add("d");
		strings.add("e");
		strings.add("f");
		
		System.out.println(strings);
		for (int i = 0; i < strings.size(); i++) {
			String el = strings.get(i);
			el+="SSSS";
			strings.set(i, el);
		}
		
		System.out.println(strings);
	}
}
