package com.ty.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ty.entity.AccountRecord;
import com.ty.service.AccountService;

@RestController
@RequestMapping("/account")
public class AccountController{

    @Autowired
    private AccountService accountService;

    @GetMapping("/healthCheck")
    public ResponseEntity<Object> getAllRichMenuList(){
        return ResponseEntity.status(HttpStatus.OK).body("server health checked!");
    }

    @GetMapping("/getAccountList")
    public ResponseEntity<Object> getAccountList(@RequestParam("name") String name){
        List<AccountRecord> result = accountService.getAccountList(name);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/login")
    public ResponseEntity<Object> login(){
        return ResponseEntity.status(HttpStatus.OK).body("Login for JWT!");
    }
}
