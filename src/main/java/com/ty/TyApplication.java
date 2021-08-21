package com.ty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@ComponentScan(basePackages = {
    "com.ty.*"
})
@SpringBootApplication
@EnableAutoConfiguration(exclude = {
    MongoAutoConfiguration.class
})
@EnableScheduling
@EnableCaching
public class TyApplication{

    public static void main(String[] args){
        SpringApplication.run(TyApplication.class, args);
    }
}
