package com.ty.controller;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

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

import com.ty.Util.DateUtils;
import com.ty.config.StockConfig;
import com.ty.service.StockService;

@PropertySource(value = "classpath:stock.properties", encoding = "UTF-8")
@RestController
@RequestMapping("/stock")
public class StockInfoController{

    private static final Logger logger = LoggerFactory.getLogger(StockInfoController.class);

    @Value("${stock_with_futures_tse}")
    String tseStockWithFutures;

    @Value("${stock_with_futures_otc}")
    String otcStockWithFutures;

    @Value("${stock_without_futures_tse}")
    String tseStockWithoutFutures;

    @Value("${stock_without_futures_otc}")
    String otcStockWithoutFutures;

    @Autowired
    private StockService stockService;

    @Autowired
    private StockConfig stockConfig;

    @GetMapping("/syncStockPriceAvgInfo")
    public ResponseEntity<Object> syncStockPriceAvgInfo() throws KeyManagementException, ClientProtocolException, NoSuchAlgorithmException, IOException{
        try{
            FutureTask<String> tseTask = new FutureTask<>(new syncStockPriceAvgInfo(1));
            FutureTask<String> Otctask = new FutureTask<>(new syncStockPriceAvgInfo(2));
            new Thread(tseTask).start();
            new Thread(Otctask).start();
            if ("OK".equals(tseTask.get()) && "OK".equals(Otctask.get())){
                return ResponseEntity.status(HttpStatus.OK).body("sync successfully!");
            }else{
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("failed!");
            }
        }catch(Exception e){
            logger.error("error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    private class syncStockPriceAvgInfo<T> implements Callable<T>{

        private int stockType;

        public syncStockPriceAvgInfo(int stockType){
            this.stockType = stockType;
        }

        @Override
        public T call() throws Exception{
            switch(stockType){
                case 1:
                    for(String stockInfo : stockConfig.tseDataSeries()){
                        String[] info = stockInfo.split(";");
                        stockService.syncStockPriceAvgInfo(info[0], stockType);
                    }
                    break;
                case 2:
                    for(String stockInfo : stockConfig.otcDataSeries()){
                        String[] info = stockInfo.split(";");
                        stockService.syncStockPriceAvgInfo(info[0], stockType);
                    }
                    break;
                default:
                    throw new Exception("invalid stock type!");
            }
            return (T) "OK";
        }
    }

    @GetMapping("/fetchStockLegalInfo")
    public ResponseEntity<Object> fetchStockLegalInfo(@RequestParam("stockType") int stockType) throws KeyManagementException, ClientProtocolException, NoSuchAlgorithmException, IOException{
        try{
            String[] dateRange = new String[]{
                DateUtils.todayDate, DateUtils.todayDate
            };
            switch(stockType){
                case 1:
                    for(String stockInfo : stockConfig.tseDataSeries()){
                        String[] info = stockInfo.split(";");
                        stockService.fetchDailyStockLegalInfo(info[0], stockType, dateRange);
                    }
                    break;
                case 2:
                    for(String stockInfo : stockConfig.otcDataSeries()){
                        String[] info = stockInfo.split(";");
                        stockService.fetchDailyStockLegalInfo(info[0], stockType, dateRange);
                    }
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

    @GetMapping("/fetchStockPriceInfo")
    public ResponseEntity<Object> fetchStockPriceInfo(@RequestParam("stockType") int stockType) throws KeyManagementException, ClientProtocolException, NoSuchAlgorithmException, IOException{
        try{
            String[] dateRange = new String[]{
                DateUtils.todayDate, DateUtils.todayDate
            };
            switch(stockType){
                case 1:
                    for(String stockInfo : stockConfig.tseDataSeries()){
                        String[] info = stockInfo.split(";");
                        stockService.processAllTypeStockByFinmind(info[0], info[1], 1, dateRange);
                    }
                    break;
                case 2:
                    for(String stockInfo : stockConfig.otcDataSeries()){
                        String[] info = stockInfo.split(";");
                        stockService.processAllTypeStockByFinmind(info[0], info[1], 2, dateRange);
                    }
                    break;
                default:
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("invalid stockType!");
            }
        }catch(Exception e){
            logger.error("request error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("fetch info failed!");
        }
        return ResponseEntity.status(HttpStatus.OK).body("fetch successfully!");
    }

    @GetMapping("/rebuildStockPriceAvgInfo")
    public ResponseEntity<Object> rebuildStockPriceAvgInfo(@RequestParam("stockType") int stockType) throws KeyManagementException, ClientProtocolException, NoSuchAlgorithmException, IOException{
        try{
            switch(stockType){
                case 1:
                    for(String stockInfo : stockConfig.tseDataSeries()){
                        String[] info = stockInfo.split(";");
                        stockService.rebuildStockPriceAvgInfo(info[0], stockType);
                    }
                    break;
                case 2:
                    for(String stockInfo : stockConfig.otcDataSeries()){
                        String[] info = stockInfo.split(";");
                        stockService.rebuildStockPriceAvgInfo(info[0], stockType);
                    }
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
}
