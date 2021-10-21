package com.ty.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.mongodb.MongoClient;

@Configuration
public class MongoDBConfig{

    @Value("${spring.data.mongodb.host}")
    private String mongodbUrl;

    @Value("${spring.data.mongodb.port}")
    private int mongodbPort;

    @Bean
    public MongoClient mongoDBClient(){
        MongoClient mongoClient = new MongoClient(mongodbUrl, mongodbPort);
        return mongoClient;
    }
}
