package com.ty.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class AwsConfig{

    @Bean
    public AmazonS3 s3(){
        AWSCredentials awsCredentials = new BasicAWSCredentials("access_key", "secret_key");
        return AmazonS3ClientBuilder
            .standard()
            .withRegion("ap-northeast-1")
            .withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).build();
    }
}
