package com.ty.controller;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.json.*;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.ty.Util.DateUtils;
import com.ty.entity.AccountRecord;
import com.ty.service.AccountService;
import com.ty.service.StockService;
import com.ty.vo.SingleStockInfo;

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

    @GetMapping("/syncStockPriceAvgInfo")
    public ResponseEntity<Object> syncStockPriceAvgInfo(@RequestParam("stockType") int stockType) throws KeyManagementException, ClientProtocolException, NoSuchAlgorithmException, IOException{
        try{
            switch(stockType){
                case 1:
                    //stockService.syncStockPriceAvgInfo("2603", stockType);
                    String[] tseWithFuturesData = tseStockWithFutures.split(",");
                    String[] tseWithoutFuturesData = tseStockWithoutFutures.split(",");
                    String[] tseDataSeries = (String[]) ArrayUtils.addAll(tseWithFuturesData, tseWithoutFuturesData);
                    for(String stockInfo : tseDataSeries){
                        String[] info = stockInfo.split(";");
                        stockService.syncStockPriceAvgInfo(info[0], stockType);
                    }
                    break;
                case 2:
                    //stockService.syncStockPriceAvgInfo("3260", stockType);
                    String[] otcWithFuturesData = otcStockWithFutures.split(",");
                    String[] otcWithoutFuturesData = otcStockWithoutFutures.split(",");
                    String[] otcDataSeries = (String[]) ArrayUtils.addAll(otcWithFuturesData, otcWithoutFuturesData);
                    for(String stockInfo : otcDataSeries){
                        String[] info = stockInfo.split(";");
                        stockService.syncStockPriceAvgInfo(info[0], stockType);
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

    @GetMapping("/rebuildStockPriceAvgInfo")
    public ResponseEntity<Object> rebuildStockPriceAvgInfo(@RequestParam("stockType") int stockType) throws KeyManagementException, ClientProtocolException, NoSuchAlgorithmException, IOException{
        try{
            switch(stockType){
                case 1:
                    String[] tseWithFuturesData = tseStockWithFutures.split(",");
                    String[] tseWithoutFuturesData = tseStockWithoutFutures.split(",");
                    String[] tseDataSeries = (String[]) ArrayUtils.addAll(tseWithFuturesData, tseWithoutFuturesData);
                    for(String stockInfo : tseDataSeries){
                        String[] info = stockInfo.split(";");
                        stockService.rebuildStockPriceAvgInfo(info[0], stockType);
                    }
                    break;
                case 2:
                    String[] otcWithFuturesData = otcStockWithFutures.split(",");
                    String[] otcWithoutFuturesData = otcStockWithoutFutures.split(",");
                    String[] otcDataSeries = (String[]) ArrayUtils.addAll(otcWithFuturesData, otcWithoutFuturesData);
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

    @GetMapping("/fetchStockLegalInfo")
    public ResponseEntity<Object> fetchStockLegalInfo(@RequestParam("stockType") int stockType) throws KeyManagementException, ClientProtocolException, NoSuchAlgorithmException, IOException{
        try{
            String[] dateRange = new String[]{
                DateUtils.todayDate, DateUtils.todayDate
            };
            switch(stockType){
                case 1:
                    String[] tseWithFuturesData = tseStockWithFutures.split(",");
                    String[] tseWithoutFuturesData = tseStockWithoutFutures.split(",");
                    String[] tseDataSeries = (String[]) ArrayUtils.addAll(tseWithFuturesData, tseWithoutFuturesData);
                    for(String stockInfo : tseDataSeries){
                        String[] info = stockInfo.split(";");
                        stockService.fetchDailyStockLegalInfo(info[0], stockType, dateRange);
                    }
                    break;
                case 2:
                    String[] otcWithFuturesData = otcStockWithFutures.split(",");
                    String[] otcWithoutFuturesData = otcStockWithoutFutures.split(",");
                    String[] otcDataSeries = (String[]) ArrayUtils.addAll(otcWithFuturesData, otcWithoutFuturesData);
                    for(String stockInfo : otcDataSeries){
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
                    String[] tseWithFuturesData = tseStockWithFutures.split(",");
                    String[] tseWithoutFuturesData = tseStockWithoutFutures.split(",");
                    String[] tseDataSeries = (String[]) ArrayUtils.addAll(tseWithFuturesData, tseWithoutFuturesData);
                    for(String stockInfo : tseDataSeries){
                        String[] info = stockInfo.split(";");
                        stockService.processAllTypeStockByFinmind(info[0], info[1], 1, dateRange);
                    }
                    break;
                case 2:
                    String[] otcWithFuturesData = otcStockWithFutures.split(",");
                    String[] otcWithoutFuturesData = otcStockWithoutFutures.split(",");
                    String[] otcDataSeries = (String[]) ArrayUtils.addAll(otcWithFuturesData, otcWithoutFuturesData);
                    for(String stockInfo : otcDataSeries){
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
}
