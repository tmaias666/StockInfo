package com.ty.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ty.service.LineMessageService;
import com.ty.service.StockStrategyService;

@RestController
@RequestMapping("/line")
public class LineMessageController{

    private static final Logger logger = LoggerFactory.getLogger(LineMessageController.class);

    @Autowired
    StockStrategyService stockStrategyService;

    @Autowired
    LineMessageService lineMessageService;

    @GetMapping("/sendMessage")
    public ResponseEntity<Object> sendMessage(){
        try{
            lineMessageService.sendDailyStrategyResultMessage();
        }catch(Exception e){
            logger.error("error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).body("send successfully!");
    }

    @PostMapping("/webhook")
    public ResponseEntity<Object> getWebhook(@RequestBody String json){
        try{
            logger.info("webhook request body: " + json);
            lineMessageService.parseUserInfoAndReply(json);
        }catch(Exception e){
            logger.error("error: ", e);
        }
        return ResponseEntity.status(HttpStatus.OK).body("parse successfully!");
    }
}
