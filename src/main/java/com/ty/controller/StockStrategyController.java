package com.ty.controller;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ty.service.StockStrategyService;

@PropertySource(value = "classpath:stock.properties", encoding = "UTF-8")
@RestController
@RequestMapping("/strategy")
public class StockStrategyController{

    private static final Logger logger = LoggerFactory.getLogger(StockStrategyController.class);

    @Value("${stock_with_futures_tse}")
    String tseStockWithFutures;

    @Value("${stock_with_futures_otc}")
    String otcStockWithFutures;

    @Value("${stock_without_futures_tse}")
    String tseStockWithoutFutures;

    @Value("${stock_without_futures_otc}")
    String otcStockWithoutFutures;

    @Autowired
    private StockStrategyService stockStrategyService;

    @GetMapping("/getStockStrategyWeekly")
    public ResponseEntity<Object> getStockStrategyWeekly() throws KeyManagementException, ClientProtocolException, NoSuchAlgorithmException, IOException{
        try{
            stockStrategyService.generateWeeklyStrategyReport();
        }catch(Exception e){
            logger.error("error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).body("fetch successfully!");
    }

    @GetMapping("/getStockStrategyDaily")
    public ResponseEntity<Object> getStockStrategyDaily(@RequestParam("strategyType") int strategyType, @RequestParam("stockType") int stockType) throws KeyManagementException, ClientProtocolException, NoSuchAlgorithmException, IOException{
        try{
            switch(strategyType){
                case 1://bull
                    stockStrategyService.generateBullStrategyReport(stockType);
                    break;
                case 2://bear
                    stockStrategyService.generateBearStrategyReport(stockType);
                    break;
                default:
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("invalid stockType!");
            }
        }catch(Exception e){
            logger.error("error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).body("fetch successfully!");
    }

    @GetMapping("/getCustomStockStrategy1")
    public ResponseEntity<Object> getCustomStockStrategy1() throws KeyManagementException, ClientProtocolException, NoSuchAlgorithmException, IOException{
        try{
            //stockStrategyService
        }catch(Exception e){
            logger.error("error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).body("fetch successfully!");
    }
}
