package com.ty.controller;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

@RestController
@RequestMapping("/mongo")
public class MongoController{

    private static final Logger logger = LoggerFactory.getLogger(MongoController.class);

    @Value("${spring.data.mongodb.host}")
    private String mongodbUrl;

    @Value("${spring.data.mongodb.port}")
    private int mongodbPort;

    @GetMapping("/test")
    public ResponseEntity<Object> getAllRichMenuList(){
        MongoClient mongoClient = null;
        try{
            mongoClient = new MongoClient(mongodbUrl, mongodbPort);
            MongoDatabase database = mongoClient.getDatabase("stock");
            MongoCollection<Document> collection = database.getCollection("testCollection");
            logger.info("當前DB中的所有集合是：");
            for(String name : database.listCollectionNames()){
                logger.info(name);
            }
            FindIterable<Document> findIterable = collection.find();
            MongoCursor<Document> mongoCursor = findIterable.iterator();
            while (mongoCursor.hasNext()){
                Document doc = mongoCursor.next();
                logger.info("doc: " + doc.toJson());
                return ResponseEntity.status(HttpStatus.OK).body("doc: " + doc.toJson());
            }
            return ResponseEntity.status(HttpStatus.OK).body("doc is empty!");
        }catch(Exception e){
            logger.error("test error: " + e.getMessage());
        }finally{
            mongoClient.close();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error!");
    }
}
