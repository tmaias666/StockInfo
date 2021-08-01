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

    @GetMapping("/testUploadToS3")
    public ResponseEntity<Object> uploadToS3(){
        try{
            //awsService.uploadToS3();
            return ResponseEntity.status(HttpStatus.OK).body("upload to S3 successfully!");
        }catch(Exception e){
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("upload to S3 error!");
        }
    }

    @GetMapping("/testDownloadFromS3")
    public ResponseEntity<Object> downloadFromS3(){
        try{
            awsService.downloadFromS3("test_file");
            return ResponseEntity.status(HttpStatus.OK).body("download from S3 successfully!");
        }catch(Exception e){
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("download from S3 error!");
        }
    }
}
