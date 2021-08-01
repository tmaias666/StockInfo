package com.ty.service;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class AwsService{

    private static final Logger logger = LoggerFactory.getLogger(AwsService.class);

    @Autowired
    private AmazonS3 s3;

    private static final String bucketName = "tytest2021";

    public void uploadToS3(String filePath, String fileName){
        PutObjectRequest request = new PutObjectRequest(bucketName, fileName, new File(filePath));
        ObjectMetadata metadata = new ObjectMetadata();
        //metadata.setContentType(contentType);
        request.setMetadata(metadata);
        //ACL
        //https://docs.aws.amazon.com/zh_tw/AmazonS3/latest/dev/acl-overview.html 
        //request.setCannedAcl(CannedAccessControlList.PublicRead);
        s3.putObject(request);
        logger.info("上傳成功: " + fileName);
    }

    public void downloadFromS3(String fileName) throws FileNotFoundException, IOException{
        S3Object s3Object = s3.getObject(bucketName, fileName);
        ObjectMetadata objectMetadata = s3Object.getObjectMetadata();
        System.out.println("contentType: " + objectMetadata.getContentType());
        IOUtils.copy(s3Object.getObjectContent(), new FileOutputStream(new File("D:\\stock\\downloadFromS3")));
    }
}
