package com.ty.service;

import org.apache.tomcat.util.http.fileupload.IOUtils;
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

    @Autowired
    private AmazonS3 s3;

    private static final String bucketName = "tytest2021";

    public void uploadToS3(){
        PutObjectRequest request = new PutObjectRequest(bucketName, "root_key", new File("D:\\stock\\stock.csv"));
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("image/jpg");
        request.setMetadata(metadata);
        //ACL
        //https://docs.aws.amazon.com/zh_tw/AmazonS3/latest/dev/acl-overview.html 
        //request.setCannedAcl(CannedAccessControlList.PublicRead);
        s3.putObject(request);
        System.out.println("上傳成功!");
    }

    public void downloadFromS3() throws FileNotFoundException, IOException{
        S3Object s3Object = s3.getObject(bucketName, "root_key");
        ObjectMetadata objectMetadata = s3Object.getObjectMetadata();
        System.out.println("contentType: " + objectMetadata.getContentType());
        IOUtils.copy(s3Object.getObjectContent(), new FileOutputStream(new File("D:\\stock\\downloadFromS3")));
    }
}
