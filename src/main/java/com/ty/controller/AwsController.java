package com.ty.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ty.service.AwsService;

@RestController
@RequestMapping("/aws")
public class AwsController{

    @Autowired
    AwsService awsService;

    @GetMapping("/uploadToS3")
    public ResponseEntity<Object> uploadToS3(){
        awsService.uploadToS3();
        return ResponseEntity.status(HttpStatus.OK).body("uploadToS3 OK!");
    }

    @GetMapping("/downloadFromS3")
    public ResponseEntity<Object> downloadFromS3(){
        try{
            awsService.downloadFromS3();
            return ResponseEntity.status(HttpStatus.OK).body("downloadFromS3 OK!");
        }catch(Exception e){
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("downloadFromS3 error!");
        }
    }
}
