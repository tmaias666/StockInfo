package com.ty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {
    "com.ty.*"
})
@SpringBootApplication
@EnableAutoConfiguration(exclude = {
    MongoAutoConfiguration.class
})
public class TyApplication{

    public static void main(String[] args){
        SpringApplication.run(TyApplication.class, args);
    }
}
