package com.ty.controller;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ty.entity.LineUserAccount;
import com.ty.mybatis.mapper.LineUserAccountMapper;

@RestController
@RequestMapping("/account")
public class AccountController{

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    LineUserAccountMapper lineUserAccountMapper;

    @GetMapping("/healthCheck")
    public ResponseEntity<Object> healthCheck(){
        return ResponseEntity.status(HttpStatus.OK).body("server health checked!");
    }

    @GetMapping("/login")
    public ResponseEntity<Object> login(){
        return ResponseEntity.status(HttpStatus.OK).body("Login for JWT!");
    }

    @GetMapping("/getUser")
    public ResponseEntity<Object> getUser(){
        LineUserAccount user = lineUserAccountMapper.findByLineUid("U11a61b18499a22d2ab9c435e05cebc2a");
        return ResponseEntity.status(HttpStatus.OK).body(user.toString());
    }
}
