package com.ty.controller;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import javax.servlet.http.HttpServletRequest;

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

import com.ty.service.StockService;
import com.ty.task.FetchStockLegalInfo;
import com.ty.task.FetchStockPriceInfo;
import com.ty.task.SyncStockPriceAvgInfo;

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
    String[] tseDataSeries;

    @Autowired
    String[] otcDataSeries;

    @GetMapping("/syncStockPriceAvgInfo")
    public ResponseEntity<Object> syncStockPriceAvgInfo() throws KeyManagementException, ClientProtocolException, NoSuchAlgorithmException, IOException{
        try{
            FutureTask<String> tseTask = new FutureTask<>(new SyncStockPriceAvgInfo(1, tseDataSeries, stockService));
            FutureTask<String> Otctask = new FutureTask<>(new SyncStockPriceAvgInfo(2, otcDataSeries, stockService));
            new Thread(tseTask).start();
            new Thread(Otctask).start();
            if ("OK".equals(tseTask.get()) && "OK".equals(Otctask.get())){
                return ResponseEntity.status(HttpStatus.OK).body("sync successfully!");
            }else{
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("failed!");
            }
        }catch(Exception e){
            logger.error("error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("sync info failed!");
        }
    }

    @GetMapping("/fetchMasterTransactionInfo")
    public ResponseEntity<Object> fetchMasterTransactionInfo() throws KeyManagementException, ClientProtocolException, NoSuchAlgorithmException, IOException{
        try{
            stockService.syncMasterTransacInfo(1);
            stockService.syncMasterTransacInfo(2);
            return ResponseEntity.status(HttpStatus.OK).body("sync successfully!");
        }catch(Exception e){
            logger.error("error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("sync info failed!");
        }
    }

    @GetMapping("/fetchStockLegalInfo")
    public ResponseEntity<Object> fetchStockLegalInfo(HttpServletRequest request, @RequestParam("stockType") int stockType) throws KeyManagementException, ClientProtocolException, NoSuchAlgorithmException, IOException{
        try{
            String accessToken = request.getSession().getAttribute("accessToken").toString();
            switch(stockType){
                case 1:
                    List<String> tseDataList = Arrays.asList(tseDataSeries);
                    FutureTask<String> tseTask1 = new FutureTask<>(new FetchStockLegalInfo(1, tseDataList.subList(0, 100), accessToken, stockService));
                    FutureTask<String> tseTask2 = new FutureTask<>(new FetchStockLegalInfo(1, tseDataList.subList(100, 200), accessToken, stockService));
                    FutureTask<String> tseTask3 = new FutureTask<>(new FetchStockLegalInfo(1, tseDataList.subList(200, 300), accessToken, stockService));
                    FutureTask<String> tseTask4 = new FutureTask<>(new FetchStockLegalInfo(1, tseDataList.subList(300, 400), accessToken, stockService));
                    FutureTask<String> tseTask5 = new FutureTask<>(new FetchStockLegalInfo(1, tseDataList.subList(400, 500), accessToken, stockService));
                    FutureTask<String> tseTask6 = new FutureTask<>(new FetchStockLegalInfo(1, tseDataList.subList(500, tseDataList.size()), accessToken, stockService));
                    new Thread(tseTask1).start();
                    new Thread(tseTask2).start();
                    new Thread(tseTask3).start();
                    new Thread(tseTask4).start();
                    new Thread(tseTask5).start();
                    new Thread(tseTask6).start();
                    if ("OK".equals(tseTask1.get()) && "OK".equals(tseTask2.get()) && "OK".equals(tseTask3.get()) && "OK".equals(tseTask4.get()) && "OK".equals(tseTask5.get()) && "OK".equals(tseTask6.get())){
                        return ResponseEntity.status(HttpStatus.OK).body("sync successfully!");
                    }else{
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("failed!");
                    }
                case 2:
                    List<String> otcDataList = Arrays.asList(otcDataSeries);
                    FutureTask<String> otcTask1 = new FutureTask<>(new FetchStockLegalInfo(2, otcDataList.subList(0, 100), accessToken, stockService));
                    FutureTask<String> otcTask2 = new FutureTask<>(new FetchStockLegalInfo(2, otcDataList.subList(100, 200), accessToken, stockService));
                    FutureTask<String> otcTask3 = new FutureTask<>(new FetchStockLegalInfo(2, otcDataList.subList(200, otcDataList.size()), accessToken, stockService));
                    new Thread(otcTask1).start();
                    new Thread(otcTask2).start();
                    new Thread(otcTask3).start();
                    if ("OK".equals(otcTask1.get()) && "OK".equals(otcTask2.get()) && "OK".equals(otcTask3.get())){
                        return ResponseEntity.status(HttpStatus.OK).body("sync successfully!");
                    }else{
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("failed!");
                    }
                default:
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("invalid stockType!");
            }
        }catch(Exception e){
            logger.error("error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("fetch info failed!");
        }
    }

    @GetMapping("/fetchStockPriceInfo")
    public ResponseEntity<Object> fetchStockPriceInfo(HttpServletRequest request, @RequestParam("stockType") int stockType) throws KeyManagementException, ClientProtocolException, NoSuchAlgorithmException, IOException{
        try{
            String accessToken = request.getSession().getAttribute("accessToken").toString();
            switch(stockType){
                case 1:
                    List<String> tseDataList = Arrays.asList(tseDataSeries);
                    FutureTask<String> tseTask1 = new FutureTask<>(new FetchStockPriceInfo(1, tseDataList.subList(0, 100), accessToken, stockService));
                    FutureTask<String> tseTask2 = new FutureTask<>(new FetchStockPriceInfo(1, tseDataList.subList(100, 200), accessToken, stockService));
                    FutureTask<String> tseTask3 = new FutureTask<>(new FetchStockPriceInfo(1, tseDataList.subList(200, 300), accessToken, stockService));
                    FutureTask<String> tseTask4 = new FutureTask<>(new FetchStockPriceInfo(1, tseDataList.subList(300, 400), accessToken, stockService));
                    FutureTask<String> tseTask5 = new FutureTask<>(new FetchStockPriceInfo(1, tseDataList.subList(400, 500), accessToken, stockService));
                    FutureTask<String> tseTask6 = new FutureTask<>(new FetchStockPriceInfo(1, tseDataList.subList(500, tseDataList.size()), accessToken, stockService));
                    new Thread(tseTask1).start();
                    new Thread(tseTask2).start();
                    new Thread(tseTask3).start();
                    new Thread(tseTask4).start();
                    new Thread(tseTask5).start();
                    new Thread(tseTask6).start();
                    if ("OK".equals(tseTask1.get()) && "OK".equals(tseTask2.get()) && "OK".equals(tseTask3.get()) && "OK".equals(tseTask4.get()) && "OK".equals(tseTask5.get()) && "OK".equals(tseTask6.get())){
                        return ResponseEntity.status(HttpStatus.OK).body("sync successfully!");
                    }else{
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("failed!");
                    }
                case 2:
                    List<String> otcDataList = Arrays.asList(otcDataSeries);
                    FutureTask<String> otcTask1 = new FutureTask<>(new FetchStockPriceInfo(2, otcDataList.subList(0, 100), accessToken, stockService));
                    FutureTask<String> otcTask2 = new FutureTask<>(new FetchStockPriceInfo(2, otcDataList.subList(100, 200), accessToken, stockService));
                    FutureTask<String> otcTask3 = new FutureTask<>(new FetchStockPriceInfo(2, otcDataList.subList(200, otcDataList.size()), accessToken, stockService));
                    new Thread(otcTask1).start();
                    new Thread(otcTask2).start();
                    new Thread(otcTask3).start();
                    if ("OK".equals(otcTask1.get()) && "OK".equals(otcTask2.get()) && "OK".equals(otcTask3.get())){
                        return ResponseEntity.status(HttpStatus.OK).body("sync successfully!");
                    }else{
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("failed!");
                    }
                default:
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("invalid stockType!");
            }
        }catch(Exception e){
            logger.error("request error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("fetch info failed!");
        }
    }

    @GetMapping("/rebuildStockPriceAvgInfo")
    public ResponseEntity<Object> rebuildStockPriceAvgInfo(@RequestParam("stockType") int stockType) throws KeyManagementException, ClientProtocolException, NoSuchAlgorithmException, IOException{
        try{
            switch(stockType){
                case 1:
                    for(String stockInfo : tseDataSeries){
                        String[] info = stockInfo.split(";");
                        stockService.rebuildStockPriceAvgInfo(info[0], stockType);
                    }
                    break;
                case 2:
                    for(String stockInfo : otcDataSeries){
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
