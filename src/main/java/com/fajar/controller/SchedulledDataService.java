package com.fajar.controller;

import java.util.Date;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.messaging.simp.broker.BrokerAvailabilityEvent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SchedulledDataService implements
    ApplicationListener<BrokerAvailabilityEvent> {
	
	Logger log = LoggerFactory.getLogger(SchedulledDataService.class);

    private final MessageSendingOperations<String> messagingTemplate;

     @Autowired
    public SchedulledDataService(final MessageSendingOperations<String> messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        log.info("------------------------Schedulled Data Service----------------------------");
    	log.info("messagingTemplate: {}",messagingTemplate);
        
    }

    @Override
    public void onApplicationEvent(final BrokerAvailabilityEvent event) {
    }

    @Scheduled(fixedDelay = 1000)
    public void sendDataUpdates() {
    //System.out.println("send Update");
        this.messagingTemplate.convertAndSend(
            "/wsResp/data", new Random().nextInt(100));

    }

    @Scheduled(fixedDelay = 1000)
    public void sendTime() {
    //System.out.println("send Update");
        this.messagingTemplate.convertAndSend(
            "/wsResp/time", new Date().toString());

    }
} 
