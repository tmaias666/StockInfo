package com.ty.schedule;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import com.ty.controller.StockInfoController;
import com.ty.entity.DailySyncLog;
import com.ty.entity.StockConfig;
import com.ty.repository.DailySyncLogRepository;
import com.ty.repository.StockConfigRepository;
import com.ty.service.StockService;
import com.ty.task.FetchStockLegalInfo;
import com.ty.task.FetchStockPriceInfo;
import com.ty.task.SyncStockPriceAvgInfo;

@Service
public class FetchStockInfoSchedule{

    private static final Logger logger = LoggerFactory.getLogger(FetchStockInfoSchedule.class);

    @Autowired
    String[] tseDataSeries;

    @Autowired
    String[] otcDataSeries;

    @Autowired
    private StockService stockService;

    @Autowired
    DailySyncLogRepository dailySyncLogRepository;

    @Autowired
    StockConfigRepository stockConfigRepository;

    private static final String token1 = "token1";

    private static final String token2 = "token2";

    private static final String token3 = "token3";
    //private ThreadPoolExecutor commonThreadPool = new ThreadPoolExecutor(10, 10, 1L, TimeUnit.MINUTES, new LinkedBlockingQueue<>());

    @Scheduled(cron = "0 20 17 ? * MON-FRI")
    public void fetchTseStockPriceInfo(){
        if (!checkIsAutoSync()){
            return;
        }
        DailySyncLog log = new DailySyncLog();
        log.setJobType("fetchTseStockPriceInfo");
        log.setInfoDate(LocalDate.now());
        log.setCreateTime(new Date());
        try{
            List<String> tseDataList = Arrays.asList(tseDataSeries);
            FutureTask<String> tseTask1 = new FutureTask<>(new FetchStockPriceInfo(1, tseDataList.subList(0, 100), token1, stockService));
            FutureTask<String> tseTask2 = new FutureTask<>(new FetchStockPriceInfo(1, tseDataList.subList(100, 200), token1, stockService));
            FutureTask<String> tseTask3 = new FutureTask<>(new FetchStockPriceInfo(1, tseDataList.subList(200, 300), token1, stockService));
            FutureTask<String> tseTask4 = new FutureTask<>(new FetchStockPriceInfo(1, tseDataList.subList(300, 400), token1, stockService));
            FutureTask<String> tseTask5 = new FutureTask<>(new FetchStockPriceInfo(1, tseDataList.subList(400, 500), token1, stockService));
            FutureTask<String> tseTask6 = new FutureTask<>(new FetchStockPriceInfo(1, tseDataList.subList(500, tseDataList.size()), token1, stockService));
            new Thread(tseTask1).start();
            new Thread(tseTask2).start();
            new Thread(tseTask3).start();
            new Thread(tseTask4).start();
            new Thread(tseTask5).start();
            new Thread(tseTask6).start();
            if ("OK".equals(tseTask1.get()) && "OK".equals(tseTask2.get()) && "OK".equals(tseTask3.get()) && "OK".equals(tseTask4.get()) && "OK".equals(tseTask5.get()) && "OK".equals(tseTask6.get())){
                log.setSyncResult("Y");
                log.setSyncMessage("OK");
            }else{
                log.setSyncResult("N");
                StringBuilder sb = new StringBuilder();
                sb.append("tseTask1:").append(tseTask1.get());
                sb.append(";tseTask2:").append(tseTask2.get());
                sb.append(";tseTask3:").append(tseTask3.get());
                sb.append(";tseTask4:").append(tseTask4.get());
                sb.append(";tseTask5:").append(tseTask5.get());
                sb.append(";tseTask6:").append(tseTask6.get());
                log.setSyncMessage(sb.toString());
            }
        }catch(Exception e){
            logger.error("fetchTseStockPriceInfo error: ", e);
            log.setSyncResult("N");
            log.setSyncMessage("error:" + e.getMessage().substring(0, 190));
        }
        dailySyncLogRepository.save(log);
    }

    @Scheduled(cron = "0 40 19 ? * MON-FRI")
    public void fetchTseStockLegalInfo(){
        if (!checkIsAutoSync()){
            return;
        }
        DailySyncLog log = new DailySyncLog();
        log.setJobType("fetchTseStockLegalInfo");
        log.setInfoDate(LocalDate.now());
        log.setCreateTime(new Date());
        try{
            List<String> tseDataList = Arrays.asList(tseDataSeries);
            FutureTask<String> tseTask1 = new FutureTask<>(new FetchStockLegalInfo(1, tseDataList.subList(0, 100), token3, stockService));
            FutureTask<String> tseTask2 = new FutureTask<>(new FetchStockLegalInfo(1, tseDataList.subList(100, 200), token3, stockService));
            FutureTask<String> tseTask3 = new FutureTask<>(new FetchStockLegalInfo(1, tseDataList.subList(200, 300), token3, stockService));
            FutureTask<String> tseTask4 = new FutureTask<>(new FetchStockLegalInfo(1, tseDataList.subList(300, 400), token3, stockService));
            FutureTask<String> tseTask5 = new FutureTask<>(new FetchStockLegalInfo(1, tseDataList.subList(400, 500), token3, stockService));
            FutureTask<String> tseTask6 = new FutureTask<>(new FetchStockLegalInfo(1, tseDataList.subList(500, tseDataList.size()), token3, stockService));
            new Thread(tseTask1).start();
            new Thread(tseTask2).start();
            new Thread(tseTask3).start();
            new Thread(tseTask4).start();
            new Thread(tseTask5).start();
            new Thread(tseTask6).start();
            if ("OK".equals(tseTask1.get()) && "OK".equals(tseTask2.get()) && "OK".equals(tseTask3.get()) && "OK".equals(tseTask4.get()) && "OK".equals(tseTask5.get()) && "OK".equals(tseTask6.get())){
                log.setSyncResult("Y");
                log.setSyncMessage("OK");
            }else{
                log.setSyncResult("N");
                StringBuilder sb = new StringBuilder();
                sb.append("tseTask1:").append(tseTask1.get());
                sb.append(";tseTask2:").append(tseTask2.get());
                sb.append(";tseTask3:").append(tseTask3.get());
                sb.append(";tseTask4:").append(tseTask4.get());
                sb.append(";tseTask5:").append(tseTask5.get());
                sb.append(";tseTask6:").append(tseTask6.get());
                log.setSyncMessage(sb.toString());
            }
        }catch(Exception e){
            logger.error("fetchTseStockLegalInfo error: ", e);
            log.setSyncResult("N");
            log.setSyncMessage("error:" + e.getMessage().substring(0, 190));
        }
        dailySyncLogRepository.save(log);
    }

    @Scheduled(cron = "0 30 18 ? * MON-FRI")
    public void fetchOtcStockPriceInfo(){
        if (!checkIsAutoSync()){
            return;
        }
        DailySyncLog log = new DailySyncLog();
        log.setJobType("fetchOtcStockPriceInfo");
        log.setInfoDate(LocalDate.now());
        log.setCreateTime(new Date());
        try{
            List<String> otcDataList = Arrays.asList(otcDataSeries);
            FutureTask<String> otcTask1 = new FutureTask<>(new FetchStockPriceInfo(2, otcDataList.subList(0, 100), token2, stockService));
            FutureTask<String> otcTask2 = new FutureTask<>(new FetchStockPriceInfo(2, otcDataList.subList(100, 200), token2, stockService));
            FutureTask<String> otcTask3 = new FutureTask<>(new FetchStockPriceInfo(2, otcDataList.subList(200, otcDataList.size()), token2, stockService));
            new Thread(otcTask1).start();
            new Thread(otcTask2).start();
            new Thread(otcTask3).start();
            if ("OK".equals(otcTask1.get()) && "OK".equals(otcTask2.get()) && "OK".equals(otcTask3.get())){
                log.setSyncResult("Y");
                log.setSyncMessage("OK");
                log.setCreateTime(new Date());
            }else{
                log.setSyncResult("N");
                StringBuilder sb = new StringBuilder();
                sb.append("otcTask1:").append(otcTask1.get());
                sb.append(";otcTask2:").append(otcTask2.get());
                sb.append(";otcTask3:").append(otcTask3.get());
                log.setSyncMessage(sb.toString());
            }
        }catch(Exception e){
            logger.error("fetchOtcStockPriceInfo error: ", e);
            log.setSyncResult("N");
            log.setSyncMessage("error:" + e.getMessage().substring(0, 190));
        }
        dailySyncLogRepository.save(log);
    }

    @Scheduled(cron = "0 30 18 ? * MON-FRI")
    public void fetchOtcStockLegalInfo(){
        if (!checkIsAutoSync()){
            return;
        }
        DailySyncLog log = new DailySyncLog();
        log.setJobType("fetchOtcStockLegalInfo");
        log.setInfoDate(LocalDate.now());
        log.setCreateTime(new Date());
        try{
            List<String> otcDataList = Arrays.asList(otcDataSeries);
            FutureTask<String> otcTask1 = new FutureTask<>(new FetchStockLegalInfo(2, otcDataList.subList(0, 100), token2, stockService));
            FutureTask<String> otcTask2 = new FutureTask<>(new FetchStockLegalInfo(2, otcDataList.subList(100, 200), token2, stockService));
            FutureTask<String> otcTask3 = new FutureTask<>(new FetchStockLegalInfo(2, otcDataList.subList(200, otcDataList.size()), token2, stockService));
            new Thread(otcTask1).start();
            new Thread(otcTask2).start();
            new Thread(otcTask3).start();
            if ("OK".equals(otcTask1.get()) && "OK".equals(otcTask2.get()) && "OK".equals(otcTask3.get())){
                log.setSyncResult("Y");
                log.setSyncMessage("OK");
            }else{
                log.setSyncResult("N");
                StringBuilder sb = new StringBuilder();
                sb.append("otcTask1:").append(otcTask1.get());
                sb.append(";otcTask2:").append(otcTask2.get());
                sb.append(";otcTask3:").append(otcTask3.get());
                log.setSyncMessage(sb.toString());
            }
        }catch(Exception e){
            logger.error("fetchOtcStockLegalInfo error: ", e);
            log.setSyncResult("N");
            log.setSyncMessage("error:" + e.getMessage().substring(0, 190));
        }
        dailySyncLogRepository.save(log);
    }

    @Scheduled(cron = "0 45 19 ? * MON-FRI")
    public void syncStockPriceAvgInfo(){
        if (!checkIsAutoSync()){
            return;
        }
        DailySyncLog log = new DailySyncLog();
        log.setJobType("syncStockPriceAvgInfo");
        log.setInfoDate(LocalDate.now());
        log.setCreateTime(new Date());
        try{
            FutureTask<String> tseTask = new FutureTask<>(new SyncStockPriceAvgInfo(1, tseDataSeries, stockService));
            FutureTask<String> Otctask = new FutureTask<>(new SyncStockPriceAvgInfo(2, otcDataSeries, stockService));
            new Thread(tseTask).start();
            new Thread(Otctask).start();
            if ("OK".equals(tseTask.get()) && "OK".equals(Otctask.get())){
                log.setSyncResult("Y");
                log.setSyncMessage("OK");
                updateLatestInfoDate(LocalDate.now());
            }else{
                log.setSyncResult("N");
                StringBuilder sb = new StringBuilder();
                sb.append("tseTask:").append(tseTask.get());
                sb.append(";Otctask:").append(Otctask.get());
                log.setSyncMessage(sb.toString());
            }
        }catch(Exception e){
            logger.error("syncStockPriceAvgInfo error: ", e);
            log.setSyncResult("N");
            log.setSyncMessage("error:" + e.getMessage().substring(0, 190));
        }
        dailySyncLogRepository.save(log);
    }

    @Scheduled(cron = "0 30 19 ? * MON-FRI")
    public void fetchMasterTransactionInfo(){
        if (!checkIsAutoSync()){
            return;
        }
        DailySyncLog log = new DailySyncLog();
        log.setJobType("fetchMasterTransactionInfo");
        log.setInfoDate(LocalDate.now());
        log.setCreateTime(new Date());
        try{
            stockService.syncMasterTransacInfo();
            log.setSyncResult("Y");
            log.setSyncMessage("OK");
        }catch(Exception e){
            logger.error("fetchMasterTransactionInfo error: ", e);
            log.setSyncResult("N");
            log.setSyncMessage("error:" + e.getMessage().substring(0, 190));
        }
        dailySyncLogRepository.save(log);
    }

    @Scheduled(cron = "0 30 19 ? * MON-FRI")
    public void fetchBranchTradingInfo(){
        if (!checkIsAutoSync()){
            return;
        }
        DailySyncLog log = new DailySyncLog();
        log.setJobType("fetchBranchTradingInfo");
        log.setInfoDate(LocalDate.now());
        log.setCreateTime(new Date());
        try{
            stockService.syncBranchTradingInfo();
            log.setSyncResult("Y");
            log.setSyncMessage("OK");
        }catch(Exception e){
            logger.error("fetchBranchTradingInfo error: ", e);
            log.setSyncResult("N");
            log.setSyncMessage("error:" + e.getMessage().substring(0, 190));
        }
        dailySyncLogRepository.save(log);
    }

    private boolean checkIsAutoSync(){
        return "Y".equals(stockConfigRepository.findByConfigName("is_auto_sync").getConfigValue());
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    private void updateLatestInfoDate(LocalDate date){
        StockConfig config = stockConfigRepository.findByConfigName("latest_sync_info_date");
        config.setConfigValue(date.toString());
    }
}
