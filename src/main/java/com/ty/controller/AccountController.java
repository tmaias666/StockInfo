package com.ty.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
public class AccountController{

    @GetMapping("/healthCheck")
    public ResponseEntity<Object> getAllRichMenuList(){
        return ResponseEntity.status(HttpStatus.OK).body("server health checked!");
    }

    @GetMapping("/login")
    public ResponseEntity<Object> login(){
        return ResponseEntity.status(HttpStatus.OK).body("Login for JWT!");
    }
}
