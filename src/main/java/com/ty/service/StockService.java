package com.ty.service;

import org.apache.commons.beanutils.BeanUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ty.Util.HttpUtils;
import com.google.gson.Gson;
import com.ty.Util.DateUtils;
import com.ty.entity.OtcDailyAvgInfo;
import com.ty.entity.OtcDailyBaseInfo;
import com.ty.entity.OtcDailyLegalInfo;
import com.ty.entity.StockMain;
import com.ty.entity.TseDailyAvgInfo;
import com.ty.entity.TseDailyBaseInfo;
import com.ty.entity.TseDailyLegalInfo;
import com.ty.mapper.StockAvgInfoMapper;
import com.ty.repository.OtcDailyAvgInfoRepository;
import com.ty.repository.OtcDailyBaseInfoRepository;
import com.ty.repository.OtcDailyLegalInfoRepository;
import com.ty.repository.TseDailyAvgInfoRepository;
import com.ty.repository.TseDailyBaseInfoRepository;
import com.ty.repository.TseDailyLegalInfoRepository;
import com.ty.vo.StockDailyAvgInfo;
import com.ty.repository.StockMainRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class StockService{

    private static final Logger logger = LoggerFactory.getLogger(StockService.class);

    private static final Gson gson = new Gson();

    @Autowired
    private StockMainRepository stockMainRepository;

    @Autowired
    private TseDailyBaseInfoRepository tseDailyBaseInfoRepository;

    @Autowired
    private OtcDailyBaseInfoRepository otcDailyBaseInfoRepository;

    @Autowired
    private TseDailyAvgInfoRepository tseDailyAvgInfoRepository;

    @Autowired
    private OtcDailyAvgInfoRepository otcDailyAvgInfoRepository;

    @Autowired
    private TseDailyLegalInfoRepository tseDailyLegalInfoRepository;

    @Autowired
    private OtcDailyLegalInfoRepository otcDailyLegalInfoRepository;
    //second
    //@Autowired
    //OtcDailyBaseInfoSecondRepository otcDailyBaseInfoSecondRepository;
    //
    //@Autowired
    //OtcDailyLegalInfoSecondRepository otcDailyLegalInfoSecondRepository;
    //
    //@Autowired
    //TseDailyBaseInfoSecondRepository tseDailyBaseInfoSecondRepository;
    //
    //@Autowired
    //TseDailyLegalInfoSecondRepository tseDailyLegalInfoSecondRepository;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void executeMigration() throws Exception{
        //        List<TseDailyBaseInfo> tdbiList = tseDailyBaseInfoRepository.findAll();
        //        List<TseDailyBaseInfoSecond> tdbisList = new ArrayList<TseDailyBaseInfoSecond>();
        //        for(TseDailyBaseInfo tdbi : tdbiList){
        //            TseDailyBaseInfoSecond tdbis = new TseDailyBaseInfoSecond();
        //            BeanUtils.copyProperties(tdbis, tdbi);
        //            tdbis.setId(null);
        //            tdbisList.add(tdbis);
        //        }
        //        tseDailyBaseInfoSecondRepository.saveAll(tdbisList);
        //        List<TseDailyLegalInfo> tdliList = tseDailyLegalInfoRepository.findAll();
        //        List<TseDailyLegalInfoSecond> tdlisList = new ArrayList<TseDailyLegalInfoSecond>();
        //        for(TseDailyLegalInfo tdli : tdliList){
        //            TseDailyLegalInfoSecond tdlis = new TseDailyLegalInfoSecond();
        //            BeanUtils.copyProperties(tdlis, tdli);
        //            tdlis.setId(null);
        //            tdlisList.add(tdlis);
        //        }
        //        tseDailyLegalInfoSecondRepository.saveAll(tdlisList);
        //        List<OtcDailyLegalInfo> odliList = otcDailyLegalInfoRepository.findAll();
        //        List<OtcDailyLegalInfoSecond> odlisList = new ArrayList<OtcDailyLegalInfoSecond>();
        //        for(OtcDailyLegalInfo odli : odliList){
        //            OtcDailyLegalInfoSecond odlis = new OtcDailyLegalInfoSecond();
        //            BeanUtils.copyProperties(odlis, odli);
        //            odlis.setId(null);
        //            odlisList.add(odlis);
        //        }
        //        otcDailyLegalInfoSecondRepository.saveAll(odlisList);
        //        List<OtcDailyBaseInfo> odbiList = otcDailyBaseInfoRepository.findAll();
        //        List<OtcDailyBaseInfoSecond> odbisList = new ArrayList<OtcDailyBaseInfoSecond>();
        //        for(OtcDailyBaseInfo odbi : odbiList){
        //            OtcDailyBaseInfoSecond odbis = new OtcDailyBaseInfoSecond();
        //            BeanUtils.copyProperties(odbis, odbi);
        //            odbis.setId(null);
        //            odbisList.add(odbis);
        //        }
        //        otcDailyBaseInfoSecondRepository.saveAll(odbisList);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void syncStockPriceAvgInfo(String[] stockSeries, int stockType) throws Exception{
        List saveList = new ArrayList<>();
        if (stockType == 1){
            saveList = new ArrayList<TseDailyAvgInfo>();
        }else if (stockType == 2){
            saveList = new ArrayList<OtcDailyAvgInfo>();
        }
        for(int i = 0; i < stockSeries.length; i++){
            String[] info = stockSeries[i].split(";");
            String stockNo = info[0];
            List<Map<String, Object>> dailyEndPriceList;
            if (stockType == 1){
                dailyEndPriceList = tseDailyBaseInfoRepository.getEndPriceInfoForDailySync(stockNo);
            }else if (stockType == 2){
                dailyEndPriceList = otcDailyBaseInfoRepository.getEndPriceInfoForDailySync(stockNo);
            }else{
                throw new Exception("invalid stockType!");
            }
            Date now = new Date();
            Deque<Double> tempQueue = new ArrayDeque<Double>();
            double todayEndPrice = Double.valueOf(dailyEndPriceList.get(0).get("endPrice").toString());
            StockDailyAvgInfo sdai = new StockDailyAvgInfo();
            sdai.setStockNo(stockNo);
            sdai.setCreateTime(now);
            sdai.setUpdateTime(now);
            sdai.setInfoDate(LocalDate.now());
            //歷史資料不夠set default
            sdai.setSeasonAvgPrice((double) 0);
            sdai.setSeasonAvgDirection(0);
            sdai.setHalfYearAvgPrice((double) 0);
            sdai.setHalfYearAvgDirection(0);
            sdai.setYearAvgPrice((double) 0);
            sdai.setYearAvgDirection(0);
            for(int j = 0; j < dailyEndPriceList.size(); j++){
                Map<String, Object> tempMap = dailyEndPriceList.get(j);
                tempQueue.add(Double.valueOf(tempMap.get("endPrice").toString()));
                switch(j){
                    case 4://算5日均
                        sdai.setFiveAvgPrice(countAvgPriceInfo(tempQueue));
                        break;
                    case 5://比5日上揚或下彎
                        sdai.setFiveAvgDirection(getAvgDirection(todayEndPrice, tempQueue.peekLast()));
                        break;
                    case 9://算10日均
                        sdai.setTenAvgPrice(countAvgPriceInfo(tempQueue));
                        break;
                    case 10://比10日上揚或下彎
                        sdai.setTenAvgDirection(getAvgDirection(todayEndPrice, tempQueue.peekLast()));
                        break;
                    case 19://算20日均+布林上下軌
                        sdai.setMonthAvgPrice(countAvgPriceInfo(tempQueue));
                        double sd = getStandardDeviaction(tempQueue) * 2;
                        sdai.setBollingerTop(new BigDecimal(sdai.getMonthAvgPrice() + sd).setScale(2, RoundingMode.HALF_UP).doubleValue());
                        sdai.setBollingerBottom(new BigDecimal(sdai.getMonthAvgPrice() - sd).setScale(2, RoundingMode.HALF_UP).doubleValue());
                        break;
                    case 20://比20日上揚或下彎
                        sdai.setMonthAvgDirection(getAvgDirection(todayEndPrice, tempQueue.peekLast()));
                        break;
                    case 59://算60日均
                        sdai.setSeasonAvgPrice(countAvgPriceInfo(tempQueue));
                        break;
                    case 60://比60日上揚或下彎
                        sdai.setSeasonAvgDirection(getAvgDirection(todayEndPrice, tempQueue.peekLast()));
                        break;
                    case 119://算120日均
                        sdai.setHalfYearAvgPrice(countAvgPriceInfo(tempQueue));
                        break;
                    case 120://比120日上揚或下彎
                        sdai.setHalfYearAvgDirection(getAvgDirection(todayEndPrice, tempQueue.peekLast()));
                        break;
                    case 239://算240日均
                        sdai.setYearAvgPrice(countAvgPriceInfo(tempQueue));
                        break;
                    case 240://比240日上揚或下彎
                        sdai.setYearAvgDirection(getAvgDirection(todayEndPrice, tempQueue.peekLast()));
                        break;
                    default:
                        break;
                }
            }
            logger.info(gson.toJson(sdai));
            if (stockType == 1){
                TseDailyAvgInfo tdai = new TseDailyAvgInfo();
                BeanUtils.copyProperties(tdai, sdai);
                saveList.add(tdai);
                if (i == stockSeries.length - 1){
                    tseDailyAvgInfoRepository.saveAll(saveList);
                }
            }else if (stockType == 2){
                OtcDailyAvgInfo odai = new OtcDailyAvgInfo();
                BeanUtils.copyProperties(odai, sdai);
                saveList.add(odai);
                if (i == stockSeries.length - 1){
                    otcDailyAvgInfoRepository.saveAll(saveList);
                }
            }
        }
    }

    private int getAvgDirection(double todayEndPrice, double comparedPrice) throws Exception{
        if (comparedPrice == 0 || comparedPrice == todayEndPrice){
            return 0;//持平
        }else if (todayEndPrice > comparedPrice){
            return 1;//上翹
        }else if (todayEndPrice < comparedPrice){
            return 2;//下彎
        }else{
            throw new Exception("tempPriceForDirection error!");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void rebuildStockPriceAvgInfo(String stockNo, int stockType) throws Exception{
        List<Map<String, Object>> dailyEndPriceList;
        if (stockType == 1){
            dailyEndPriceList = tseDailyBaseInfoRepository.getDailyEndPriceInfoForRebuild(stockNo);
        }else if (stockType == 2){
            dailyEndPriceList = otcDailyBaseInfoRepository.getDailyEndPriceInfoForRebuild(stockNo);
        }else{
            throw new Exception("invalid stockType!");
        }
        Deque<Double> tempQueue = new ArrayDeque<Double>();
        Map<String, List<Double>> result = new HashMap<>();
        double tempPriceForDirection = 0;
        for(int i = 0; i < dailyEndPriceList.size(); i++){
            Map<String, Object> tempMap = dailyEndPriceList.get(i);
            tempQueue.add(Double.valueOf(tempMap.get("endPrice").toString()));
            //算5日
            if (tempQueue.size() == 5){
                List<Double> avgPrices = new ArrayList<Double>();
                avgPrices.add(countAvgPriceInfo(tempQueue));
                if (tempPriceForDirection == 0 || tempPriceForDirection == tempQueue.peekLast()){
                    avgPrices.add((double) 0);//持平
                }else if (tempQueue.peekLast() > tempPriceForDirection){
                    avgPrices.add((double) 1);//上翹
                }else if (tempQueue.peekLast() < tempPriceForDirection){
                    avgPrices.add((double) 2);//下彎
                }else{
                    throw new Exception("tempPriceForDirection error!");
                }
                result.put(tempMap.get("infoDate").toString(), avgPrices);
                tempPriceForDirection = tempQueue.pollFirst();
            }
        }
        tempPriceForDirection = 0;
        tempQueue.clear();
        for(int i = 0; i < dailyEndPriceList.size(); i++){
            Map<String, Object> tempMap = dailyEndPriceList.get(i);
            tempQueue.add(Double.valueOf(tempMap.get("endPrice").toString()));
            //算10日
            if (tempQueue.size() == 10){
                List<Double> existList = result.get(tempMap.get("infoDate").toString());
                existList.add(countAvgPriceInfo(tempQueue));
                if (tempPriceForDirection == 0 || tempPriceForDirection == tempQueue.peekLast()){
                    existList.add((double) 0);//持平
                }else if (tempQueue.peekLast() > tempPriceForDirection){
                    existList.add((double) 1);//上翹
                }else if (tempQueue.peekLast() < tempPriceForDirection){
                    existList.add((double) 2);//下彎
                }else{
                    throw new Exception("tempPriceForDirection error!");
                }
                result.put(tempMap.get("infoDate").toString(), existList);
                tempPriceForDirection = tempQueue.pollFirst();
            }
        }
        tempPriceForDirection = 0;
        tempQueue.clear();
        for(int i = 0; i < dailyEndPriceList.size(); i++){
            Map<String, Object> tempMap = dailyEndPriceList.get(i);
            tempQueue.add(Double.valueOf(tempMap.get("endPrice").toString()));
            //算20日+布林上下軌
            if (tempQueue.size() == 20){
                List<Double> existList = result.get(tempMap.get("infoDate").toString());
                double twentyAvgPrice = countAvgPriceInfo(tempQueue);
                existList.add(twentyAvgPrice);
                if (tempPriceForDirection == 0 || tempPriceForDirection == tempQueue.peekLast()){
                    existList.add((double) 0);//持平
                }else if (tempQueue.peekLast() > tempPriceForDirection){
                    existList.add((double) 1);//上翹
                }else if (tempQueue.peekLast() < tempPriceForDirection){
                    existList.add((double) 2);//下彎
                }else{
                    throw new Exception("tempPriceForDirection error!");
                }
                double sd = getStandardDeviaction(tempQueue) * 2;
                existList.add(new BigDecimal(twentyAvgPrice + sd).setScale(2, RoundingMode.HALF_UP).doubleValue());
                existList.add(new BigDecimal(twentyAvgPrice - sd).setScale(2, RoundingMode.HALF_UP).doubleValue());
                result.put(tempMap.get("infoDate").toString(), existList);
                tempPriceForDirection = tempQueue.pollFirst();
            }
        }
        tempPriceForDirection = 0;
        tempQueue.clear();
        for(int i = 0; i < dailyEndPriceList.size(); i++){
            Map<String, Object> tempMap = dailyEndPriceList.get(i);
            tempQueue.add(Double.valueOf(tempMap.get("endPrice").toString()));
            //算60日
            if (tempQueue.size() == 60){
                List<Double> existList = result.get(tempMap.get("infoDate").toString());
                existList.add(countAvgPriceInfo(tempQueue));
                if (tempPriceForDirection == 0 || tempPriceForDirection == tempQueue.peekLast()){
                    existList.add((double) 0);//持平
                }else if (tempQueue.peekLast() > tempPriceForDirection){
                    existList.add((double) 1);//上翹
                }else if (tempQueue.peekLast() < tempPriceForDirection){
                    existList.add((double) 2);//下彎
                }else{
                    throw new Exception("tempPriceForDirection error!");
                }
                result.put(tempMap.get("infoDate").toString(), existList);
                tempPriceForDirection = tempQueue.pollFirst();
            }
        }
        tempPriceForDirection = 0;
        tempQueue.clear();
        for(int i = 0; i < dailyEndPriceList.size(); i++){
            Map<String, Object> tempMap = dailyEndPriceList.get(i);
            tempQueue.add(Double.valueOf(tempMap.get("endPrice").toString()));
            //算120日
            if (tempQueue.size() == 120){
                List<Double> existList = result.get(tempMap.get("infoDate").toString());
                existList.add(countAvgPriceInfo(tempQueue));
                if (tempPriceForDirection == 0 || tempPriceForDirection == tempQueue.peekLast()){
                    existList.add((double) 0);//持平
                }else if (tempQueue.peekLast() > tempPriceForDirection){
                    existList.add((double) 1);//上翹
                }else if (tempQueue.peekLast() < tempPriceForDirection){
                    existList.add((double) 2);//下彎
                }else{
                    throw new Exception("tempPriceForDirection error!");
                }
                result.put(tempMap.get("infoDate").toString(), existList);
                tempPriceForDirection = tempQueue.pollFirst();
            }
        }
        tempPriceForDirection = 0;
        tempQueue.clear();
        for(int i = 0; i < dailyEndPriceList.size(); i++){
            Map<String, Object> tempMap = dailyEndPriceList.get(i);
            tempQueue.add(Double.valueOf(tempMap.get("endPrice").toString()));
            //算240日
            if (tempQueue.size() == 240){
                List<Double> existList = result.get(tempMap.get("infoDate").toString());
                existList.add(countAvgPriceInfo(tempQueue));
                if (tempPriceForDirection == 0 || tempPriceForDirection == tempQueue.peekLast()){
                    existList.add((double) 0);//持平
                }else if (tempQueue.peekLast() > tempPriceForDirection){
                    existList.add((double) 1);//上翹
                }else if (tempQueue.peekLast() < tempPriceForDirection){
                    existList.add((double) 2);//下彎
                }else{
                    throw new Exception("tempPriceForDirection error!");
                }
                result.put(tempMap.get("infoDate").toString(), existList);
                tempPriceForDirection = tempQueue.pollFirst();
            }
        }
        logger.info("同步個股: " + stockNo + ",天數:  " + result.size());
        logger.info(result.toString());
        //[199.4, 1.0, 196.8, 1.0, 197.7, 2.0, 210.95, 184.45, 202.84, 2.0, 191.0, 1.0, 166.63, 1.0]
        //[5ma,5ma方向,10ma,10ma方向,20ma,20ma方向,布林上軌,布林下軌,60ma,60ma方向,120ma,120ma方向,240ma,24ma方向]
        //寫DB
        Date now = new Date();
        if (stockType == 1){
            List<TseDailyAvgInfo> tdaiList = new ArrayList<TseDailyAvgInfo>();
            for(Map.Entry<String, List<Double>> entry : result.entrySet()){
                String dateInfo = entry.getKey();
                List<Double> avgPriceList = entry.getValue();
                TseDailyAvgInfo tdai = new TseDailyAvgInfo();
                tdai.setStockNo(stockNo);
                tdai.setCreateTime(now);
                tdai.setUpdateTime(now);
                tdai.setInfoDate(LocalDate.parse(dateInfo));
                tdai.setFiveAvgPrice(avgPriceList.get(0));
                tdai.setFiveAvgDirection(avgPriceList.get(1).intValue());
                tdai.setTenAvgPrice(avgPriceList.size() > 2 ? avgPriceList.get(2) : (double) 0);
                tdai.setTenAvgDirection(avgPriceList.size() > 3 ? avgPriceList.get(3).intValue() : 0);
                tdai.setMonthAvgPrice(avgPriceList.size() > 4 ? avgPriceList.get(4) : (double) 0);
                tdai.setMonthAvgDirection(avgPriceList.size() > 5 ? avgPriceList.get(5).intValue() : 0);
                tdai.setBollingerTop(avgPriceList.size() > 6 ? avgPriceList.get(6) : (double) 0);
                tdai.setBollingerBottom(avgPriceList.size() > 7 ? avgPriceList.get(7) : (double) 0);
                tdai.setSeasonAvgPrice(avgPriceList.size() > 8 ? avgPriceList.get(8) : (double) 0);
                tdai.setSeasonAvgDirection(avgPriceList.size() > 9 ? avgPriceList.get(9).intValue() : 0);
                tdai.setHalfYearAvgPrice(avgPriceList.size() > 10 ? avgPriceList.get(10) : (double) 0);
                tdai.setHalfYearAvgDirection(avgPriceList.size() > 11 ? avgPriceList.get(11).intValue() : 0);
                tdai.setYearAvgPrice(avgPriceList.size() > 12 ? avgPriceList.get(12) : (double) 0);
                tdai.setYearAvgDirection(avgPriceList.size() > 13 ? avgPriceList.get(13).intValue() : 0);
                tdaiList.add(tdai);
            }
            Collections.sort(tdaiList, new TseDailyAvgInfoSort());
            tseDailyAvgInfoRepository.saveAll(tdaiList);
        }else if (stockType == 2){
            List<OtcDailyAvgInfo> odaiList = new ArrayList<OtcDailyAvgInfo>();
            for(Map.Entry<String, List<Double>> entry : result.entrySet()){
                String dateInfo = entry.getKey();
                List<Double> avgPriceList = entry.getValue();
                OtcDailyAvgInfo odai = new OtcDailyAvgInfo();
                odai.setStockNo(stockNo);
                odai.setCreateTime(now);
                odai.setUpdateTime(now);
                odai.setInfoDate(LocalDate.parse(dateInfo));
                odai.setFiveAvgPrice(avgPriceList.get(0));
                odai.setFiveAvgDirection(avgPriceList.get(1).intValue());
                odai.setTenAvgPrice(avgPriceList.size() > 2 ? avgPriceList.get(2) : (double) 0);
                odai.setTenAvgDirection(avgPriceList.size() > 3 ? avgPriceList.get(3).intValue() : 0);
                odai.setMonthAvgPrice(avgPriceList.size() > 4 ? avgPriceList.get(4) : (double) 0);
                odai.setMonthAvgDirection(avgPriceList.size() > 5 ? avgPriceList.get(5).intValue() : 0);
                odai.setBollingerTop(avgPriceList.size() > 6 ? avgPriceList.get(6) : (double) 0);
                odai.setBollingerBottom(avgPriceList.size() > 7 ? avgPriceList.get(7) : (double) 0);
                odai.setSeasonAvgPrice(avgPriceList.size() > 8 ? avgPriceList.get(8) : (double) 0);
                odai.setSeasonAvgDirection(avgPriceList.size() > 9 ? avgPriceList.get(9).intValue() : 0);
                odai.setHalfYearAvgPrice(avgPriceList.size() > 10 ? avgPriceList.get(10) : (double) 0);
                odai.setHalfYearAvgDirection(avgPriceList.size() > 11 ? avgPriceList.get(11).intValue() : 0);
                odai.setYearAvgPrice(avgPriceList.size() > 12 ? avgPriceList.get(12) : (double) 0);
                odai.setYearAvgDirection(avgPriceList.size() > 13 ? avgPriceList.get(13).intValue() : 0);
                odaiList.add(odai);
            }
            Collections.sort(odaiList, new OtcDailyAvgInfoSort());
            otcDailyAvgInfoRepository.saveAll(odaiList);
        }
    }

    private double countAvgPriceInfo(Deque<Double> tempQueue){
        double total = 0;
        Iterator<Double> it = tempQueue.iterator();
        while (it.hasNext()){
            total = total + it.next();
        }
        return Math.round(( total / tempQueue.size() ) * 100.0) / 100.0;
    }

    private double getStandardDeviaction(Deque<Double> tempQueue){
        double sum = 0;
        double meanValue = countAvgPriceInfo(tempQueue); //平均數
        Iterator<Double> it = tempQueue.iterator();
        while (it.hasNext()){
            sum += Math.pow(it.next() - meanValue, 2);
        }
        return Math.sqrt(sum / tempQueue.size());
    }

    class TseDailyAvgInfoSort implements Comparator<TseDailyAvgInfo>{

        @Override
        public int compare(TseDailyAvgInfo t1, TseDailyAvgInfo t2){
            return t1.getInfoDate().compareTo(t2.getInfoDate());
        }
    }

    class OtcDailyAvgInfoSort implements Comparator<OtcDailyAvgInfo>{

        @Override
        public int compare(OtcDailyAvgInfo o1, OtcDailyAvgInfo o2){
            return o1.getInfoDate().compareTo(o2.getInfoDate());
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void fetchAndSaveDailyBaseInfo(String stockNo, int stockType, String[] dateSeries) throws Exception{
        switch(stockType){
            case 1://上市股
                processTSEStock(stockNo, stockType, dateSeries);
                break;
            case 2://上櫃股
                processOTCStock(stockNo, stockType, dateSeries);
                break;
            default:
                throw new Exception("Invalid query type!");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void fetchDailyStockLegalInfo(String stockNo, int stockType, String[] dateRange, String accessToken) throws Exception{
        //撈上市櫃股
        //List<String> stockNoList = stockMainRepository.getStockNoByStockType(stockType);
        Date now = new Date();
        String result = HttpUtils.getStockLegalInfoByFinmind(stockNo, dateRange[0], dateRange[1], accessToken);
        logger.info("query result: " + result);
        JSONObject obj = new JSONObject(result);
        JSONArray arr = obj.getJSONArray("data");//每天的個股法人買賣資訊
        if (arr.isEmpty()){
            logger.error(dateRange[0] + ", stockNO: " + stockNo + " 尚無三大法人買賣資訊!");
            return;
        }
        String tempDate = "";
        if (stockType == 1){
            List<TseDailyLegalInfo> tdliList = new ArrayList<TseDailyLegalInfo>();
            TseDailyLegalInfo tempTdli = new TseDailyLegalInfo();
            for(int i = 0; i < arr.length(); i++){
                JSONObject dailyInfo = arr.getJSONObject(i);
                if (!tempDate.equals(dailyInfo.get("date").toString())){
                    if (i != 0){
                        TseDailyLegalInfo savedInfo = new TseDailyLegalInfo();
                        BeanUtils.copyProperties(savedInfo, tempTdli);
                        tdliList.add(savedInfo);
                        tempTdli = new TseDailyLegalInfo();
                    }
                    tempDate = dailyInfo.get("date").toString();
                    tempTdli.setStockNo(stockNo);
                    tempTdli.setInfoDate(LocalDate.parse(tempDate));
                    tempTdli.setCreateTime(now);
                    tempTdli.setUpdateTime(now);
                }
                String type = dailyInfo.get("name").toString();
                switch(type){
                    case "Foreign_Investor":
                        int foreignInvestor = ( Integer.valueOf(dailyInfo.get("buy").toString()) - Integer.valueOf(dailyInfo.get("sell").toString()) );
                        tempTdli.setForeignInvestor(foreignInvestor / 1000);
                        break;
                    case "Foreign_Dealer_Self":
                        break;
                    case "Investment_Trust":
                        int investmentTrust = ( Integer.valueOf(dailyInfo.get("buy").toString()) - Integer.valueOf(dailyInfo.get("sell").toString()) );
                        tempTdli.setInvestmentTrust(investmentTrust / 1000);
                        break;
                    case "Dealer_self":
                        int dealerSelf = ( Integer.valueOf(dailyInfo.get("buy").toString()) - Integer.valueOf(dailyInfo.get("sell").toString()) );
                        tempTdli.setDealerSelf(dealerSelf / 1000);
                        break;
                    case "Dealer_Hedging":
                        int dealerHedging = ( Integer.valueOf(dailyInfo.get("buy").toString()) - Integer.valueOf(dailyInfo.get("sell").toString()) );
                        tempTdli.setDealerHedging(dealerHedging / 1000);
                        break;
                    default:
                        break;
                }
            }
            tdliList.add(tempTdli);
            tseDailyLegalInfoRepository.saveAll(tdliList);
        }else if (stockType == 2){
            List<OtcDailyLegalInfo> odliList = new ArrayList<OtcDailyLegalInfo>();
            OtcDailyLegalInfo tempOdli = new OtcDailyLegalInfo();
            for(int i = 0; i < arr.length(); i++){
                JSONObject dailyInfo = arr.getJSONObject(i);
                if (!tempDate.equals(dailyInfo.get("date").toString())){
                    if (i != 0){
                        OtcDailyLegalInfo savedInfo = new OtcDailyLegalInfo();
                        BeanUtils.copyProperties(savedInfo, tempOdli);
                        odliList.add(savedInfo);
                        tempOdli = new OtcDailyLegalInfo();
                    }
                    tempDate = dailyInfo.get("date").toString();
                    tempOdli.setStockNo(stockNo);
                    tempOdli.setInfoDate(LocalDate.parse(tempDate));
                    tempOdli.setCreateTime(now);
                    tempOdli.setUpdateTime(now);
                }
                String type = dailyInfo.get("name").toString();
                switch(type){
                    case "Foreign_Investor":
                        int foreignInvestor = ( Integer.valueOf(dailyInfo.get("buy").toString()) - Integer.valueOf(dailyInfo.get("sell").toString()) );
                        tempOdli.setForeignInvestor(foreignInvestor / 1000);
                        break;
                    case "Foreign_Dealer_Self":
                        break;
                    case "Investment_Trust":
                        int investmentTrust = ( Integer.valueOf(dailyInfo.get("buy").toString()) - Integer.valueOf(dailyInfo.get("sell").toString()) );
                        tempOdli.setInvestmentTrust(investmentTrust / 1000);
                        break;
                    case "Dealer_self":
                        int dealerSelf = ( Integer.valueOf(dailyInfo.get("buy").toString()) - Integer.valueOf(dailyInfo.get("sell").toString()) );
                        tempOdli.setDealerSelf(dealerSelf / 1000);
                        break;
                    case "Dealer_Hedging":
                        int dealerHedging = ( Integer.valueOf(dailyInfo.get("buy").toString()) - Integer.valueOf(dailyInfo.get("sell").toString()) );
                        tempOdli.setDealerHedging(dealerHedging / 1000);
                        break;
                    default:
                        break;
                }
            }
            odliList.add(tempOdli);
            otcDailyLegalInfoRepository.saveAll(odliList);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void processAllTypeStockByFinmind(String stockNo, String stockName, int stockType, String[] dateRange, String accessToken) throws Exception{
        logger.info("query stockNo: " + stockNo + "; stock name: " + stockName);
        //第一次同步個股需要先做一次
        //saveStockMain(stockNo, stockName, stockType);
        Date now = new Date();
        String result = HttpUtils.getStockPriceInfoByFinmind(stockNo, dateRange[0], dateRange[1], accessToken);
        logger.info("query result: " + result);
        JSONObject obj = new JSONObject(result);
        JSONArray arr = obj.getJSONArray("data");//每天的個股資訊陣列 
        if (stockType == 1){
            List<TseDailyBaseInfo> tdbiList = new ArrayList<TseDailyBaseInfo>();
            for(int j = 0; j < arr.length(); j++){
                JSONObject dailyInfo = arr.getJSONObject(j);
                TseDailyBaseInfo tdbi = new TseDailyBaseInfo();
                tdbi.setStockNo(stockNo);
                tdbi.setInfoDate(LocalDate.parse(dailyInfo.get("date").toString()));
                tdbi.setTotalVolumn(Integer.valueOf(dailyInfo.get("Trading_Volume").toString()) / 1000);
                tdbi.setTransactionAmount(new BigDecimal(dailyInfo.get("Trading_money").toString().replaceAll(",", "")));
                tdbi.setStartPrice(Double.valueOf(dailyInfo.get("open").toString().replaceAll(",", "")));
                tdbi.setHighPrice(Double.valueOf(dailyInfo.get("max").toString().replaceAll(",", "")));
                tdbi.setLowPrice(Double.valueOf(dailyInfo.get("min").toString().replaceAll(",", "")));
                double endPrice = Double.valueOf(dailyInfo.get("close").toString().replaceAll(",", ""));
                tdbi.setEndPrice(endPrice);
                //處理漲跌點錯誤資訊
                String diffPrice = dailyInfo.get("spread").toString();
                tdbi.setDiffPrice(Double.valueOf(diffPrice));
                tdbi.setyPrice(endPrice - tdbi.getDiffPrice());
                tdbi.setkStatus(getKStatus(tdbi.getStartPrice(), tdbi.getEndPrice(), tdbi.getHighPrice(), tdbi.getLowPrice()));//算K棒
                tdbi.setCreateTime(now);
                tdbi.setUpdateTime(now);
                tdbiList.add(tdbi);
            }
            tseDailyBaseInfoRepository.saveAll(tdbiList);
        }else if (stockType == 2){
            List<OtcDailyBaseInfo> odbiList = new ArrayList<OtcDailyBaseInfo>();
            for(int j = 0; j < arr.length(); j++){
                JSONObject dailyInfo = arr.getJSONObject(j);
                OtcDailyBaseInfo odbi = new OtcDailyBaseInfo();
                odbi.setStockNo(stockNo);
                odbi.setInfoDate(LocalDate.parse(dailyInfo.get("date").toString()));
                odbi.setTotalVolumn(Integer.valueOf(dailyInfo.get("Trading_Volume").toString()) / 1000);
                odbi.setTransactionAmount(new BigDecimal(dailyInfo.get("Trading_money").toString().replaceAll(",", "")));
                odbi.setStartPrice(Double.valueOf(dailyInfo.get("open").toString().replaceAll(",", "")));
                odbi.setHighPrice(Double.valueOf(dailyInfo.get("max").toString().replaceAll(",", "")));
                odbi.setLowPrice(Double.valueOf(dailyInfo.get("min").toString().replaceAll(",", "")));
                double endPrice = Double.valueOf(dailyInfo.get("close").toString().replaceAll(",", ""));
                odbi.setEndPrice(endPrice);
                //處理漲跌點錯誤資訊
                String diffPrice = dailyInfo.get("spread").toString();
                odbi.setDiffPrice(Double.valueOf(diffPrice));
                odbi.setyPrice(endPrice - odbi.getDiffPrice());
                odbi.setkStatus(getKStatus(odbi.getStartPrice(), odbi.getEndPrice(), odbi.getHighPrice(), odbi.getLowPrice()));//算K棒
                odbi.setCreateTime(now);
                odbi.setUpdateTime(now);
                odbiList.add(odbi);
            }
            otcDailyBaseInfoRepository.saveAll(odbiList);
        }
    }

    private void processTSEStock(String stockNo, int stockType, String[] dateSeries) throws Exception{
        //check stock_main是否存在
        boolean isSavedStockMain = false;
        String stockName;
        List<TseDailyBaseInfo> dbiList = new ArrayList<TseDailyBaseInfo>();
        Date now = new Date();
        for(int i = 0; i < dateSeries.length; i++){
            Thread.sleep(6000);
            String result = HttpUtils.getTSEStockInfoByCodeAndDate(stockNo, dateSeries[i]);
            JSONObject obj = new JSONObject(result);
            String status = obj.getString("stat");
            logger.info("status: " + status);
            if (!isSavedStockMain){
                stockName = obj.getString("title").split(" ")[2];
                logger.info("stock name: " + stockName);
                saveStockMain(stockNo, stockName, stockType);
                isSavedStockMain = true;
            }
            JSONArray arr = obj.getJSONArray("data");//每天的個股資訊陣列 
            for(int j = 0; j < arr.length(); j++){
                JSONArray dailyInfoArr = arr.getJSONArray(j);
                if ("0".equals(dailyInfoArr.get(1).toString()) || "--".equals(dailyInfoArr.get(3).toString())){
                    continue;
                }
                TseDailyBaseInfo tdbi = new TseDailyBaseInfo();
                tdbi.setStockNo(stockNo);
                tdbi.setInfoDate(DateUtils.transferMinguoDateToADDate(dailyInfoArr.get(0).toString()));
                tdbi.setTotalVolumn(Integer.valueOf(dailyInfoArr.get(1).toString().replaceAll(",", "")) / 1000);
                tdbi.setTransactionAmount(new BigDecimal(dailyInfoArr.get(2).toString().replaceAll(",", "")));
                tdbi.setStartPrice(Double.valueOf(dailyInfoArr.get(3).toString().replaceAll(",", "")));
                tdbi.setHighPrice(Double.valueOf(dailyInfoArr.get(4).toString().replaceAll(",", "")));
                tdbi.setLowPrice(Double.valueOf(dailyInfoArr.get(5).toString().replaceAll(",", "")));
                double endPrice = Double.valueOf(dailyInfoArr.get(6).toString().replaceAll(",", ""));
                tdbi.setEndPrice(endPrice);
                //處理漲跌點錯誤資訊
                String diffPrice = dailyInfoArr.get(7).toString();
                if (diffPrice.contains("X")){
                    if (dbiList.size() > 0){
                        TseDailyBaseInfo lastTdbi = dbiList.get(dbiList.size() - 1);
                        tdbi.setyPrice(lastTdbi.getEndPrice());
                        tdbi.setDiffPrice(endPrice - lastTdbi.getEndPrice());
                    }else{
                        tdbi.setyPrice((double) 0);
                        tdbi.setDiffPrice((double) 0);
                    }
                }else{
                    tdbi.setDiffPrice(Double.valueOf(diffPrice));
                    tdbi.setyPrice(endPrice - tdbi.getDiffPrice());
                }
                tdbi.setkStatus(getKStatus(tdbi.getStartPrice(), tdbi.getEndPrice(), tdbi.getHighPrice(), tdbi.getLowPrice()));//算K棒
                tdbi.setCreateTime(now);
                tdbi.setUpdateTime(now);
                dbiList.add(tdbi);
            }
        }
        tseDailyBaseInfoRepository.saveAll(dbiList);
    }

    private void processOTCStock(String stockNo, int stockType, String[] dateSeries) throws Exception{
        //check stock_main是否存在
        boolean isSavedStockMain = false;
        String stockName;
        List<OtcDailyBaseInfo> dbiList = new ArrayList<OtcDailyBaseInfo>();
        Date now = new Date();
        for(int i = 0; i < dateSeries.length; i++){
            if (i % 2 == 0){
                Thread.sleep(5000);
            }
            String result = HttpUtils.getOTCStockInfoByCodeAndDate(stockNo, dateSeries[i]);
            JSONObject obj = new JSONObject(result);
            if (!isSavedStockMain){
                stockName = obj.getString("stkName");
                logger.info("stock name: " + stockName);
                saveStockMain(stockNo, stockName, stockType);
                isSavedStockMain = true;
            }
            JSONArray arr = obj.getJSONArray("aaData");//每天的個股資訊陣列 
            for(int j = 0; j < arr.length(); j++){
                JSONArray dailyInfoArr = arr.getJSONArray(j);
                //check是否暫停交易,若停牌則當天資料不記入
                if ("0".equals(dailyInfoArr.get(1).toString()) || "--".equals(dailyInfoArr.get(3).toString())){
                    continue;
                }
                dailyInfoArr.get(0).toString();//date
                OtcDailyBaseInfo odbi = new OtcDailyBaseInfo();
                odbi.setStockNo(stockNo);
                odbi.setInfoDate(DateUtils.transferMinguoDateToADDate(dailyInfoArr.get(0).toString()));
                odbi.setTotalVolumn(Integer.valueOf(dailyInfoArr.get(1).toString().replaceAll(",", "")));//張數
                odbi.setTransactionAmount(new BigDecimal(dailyInfoArr.get(2).toString().replaceAll(",", "")));
                odbi.setStartPrice(Double.valueOf(dailyInfoArr.get(3).toString().replaceAll(",", "")));
                odbi.setHighPrice(Double.valueOf(dailyInfoArr.get(4).toString().replaceAll(",", "")));
                odbi.setLowPrice(Double.valueOf(dailyInfoArr.get(5).toString().replaceAll(",", "")));
                double endPrice = Double.valueOf(dailyInfoArr.get(6).toString().replaceAll(",", ""));
                odbi.setEndPrice(endPrice);
                odbi.setDiffPrice(Double.valueOf(dailyInfoArr.get(7).toString()));
                odbi.setyPrice(endPrice + odbi.getDiffPrice());
                odbi.setkStatus(getKStatus(odbi.getStartPrice(), odbi.getEndPrice(), odbi.getHighPrice(), odbi.getLowPrice()));//算K棒
                odbi.setCreateTime(now);
                odbi.setUpdateTime(now);
                dbiList.add(odbi);
            }
        }
        otcDailyBaseInfoRepository.saveAll(dbiList);
    }

    private int getKStatus(double startPrice, double endPrice, double highPrice, double lowPrice){
        if (startPrice > endPrice){
            return 0;//黑K
        }else if (startPrice < endPrice){
            return 1;//紅K
        }else if (( startPrice == endPrice ) && ( startPrice == highPrice ) && ( startPrice == lowPrice )){
            return 2;//一字線
        }else{
            return 3;//十字線可再細分墓碑或吊人線~
        }
    }

    private void saveStockMain(String stockNo, String StockName, int stockType) throws Exception{
        StockMain main = stockMainRepository.findByStockNo(stockNo);
        if (main == null){
            StockMain nenStockMain = new StockMain();
            Date now = new Date();
            nenStockMain.setStockNo(stockNo);
            nenStockMain.setStockName(StockName);
            nenStockMain.setStockType(stockType);
            nenStockMain.setStockFuture(0);
            //nenStockMain.setStockFuture(1);
            nenStockMain.setCreateTime(now);
            nenStockMain.setUpdateTime(now);
            stockMainRepository.save(nenStockMain);
        }else{
            logger.error("stockNo:" + stockNo + " exists!");
            throw new Exception("stockNo:" + stockNo + " exists!");
        }
    }
}
