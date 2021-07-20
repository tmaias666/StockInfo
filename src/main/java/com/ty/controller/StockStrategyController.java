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
import com.ty.service.StockStrategyService;
import com.ty.vo.SingleStockInfo;

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
}
