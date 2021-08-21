package com.ty.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.Broadcast;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.response.BotApiResponse;
import com.ty.Util.DateUtils;
import com.ty.repository.OtcDailyAvgInfoRepository;
import com.ty.repository.OtcDailyBaseInfoRepository;
import com.ty.repository.OtcDailyLegalInfoRepository;
import com.ty.repository.TseDailyAvgInfoRepository;
import com.ty.repository.TseDailyBaseInfoRepository;
import com.ty.repository.TseDailyLegalInfoRepository;
import com.ty.repository.StockMainRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StockStrategyService{

    private static final Logger logger = LoggerFactory.getLogger(StockStrategyService.class);

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

    @Autowired
    ReportService reportService;

    @Autowired
    AwsService awsService;

    @Autowired
    LineMessageService lineMessageService;

    @Value("${line.bot.channelToken}")
    private String channelAccessToken;

    private static final Gson gson = new Gson();

    private Comparator<Map<String, Object>> dealerHedgingComparator = new Comparator<Map<String, Object>>(){

        @Override
        public int compare(Map<String, Object> m1, Map<String, Object> m2){
            return -( Integer.valueOf(m1.get("dh").toString()) - Integer.valueOf(m2.get("dh").toString()) );
        }
    };

    private static final String bearStrategy1 = "收月季線下且外投賣";

    private static final String bullStrategy1 = "均線多排收上五均且外投買";

    private static final String weeklyBuyStrategy1 = "近一周外投買超總和排行";

    private static final String weeklySellStrategy1 = "近一周外投賣超總和排行";

    private static final String weeklyDealerStrategy1 = "近一周自營商避險買超總和排行";

    //紅k,實體k棒小於上影線的2/3,下影線小於實體k的2/3,K棒整體在5均上,20日均量1000以上
    public String getCustomStrategy1Message(LocalDate queryDate) throws Exception{
        List<Map<String, Object>> resultList = new LinkedList<Map<String, Object>>();
        List<String> tseStockNoList = new ArrayList<>();
        List<Map<String, Object>> tseTotalVolumnList = new ArrayList<>();
        List<Map<String, Object>> tseList = tseDailyBaseInfoRepository.getCustomStrategy1(queryDate);
        if (tseList.size() != 0){
            Iterator tseIt = tseList.iterator();
            while (tseIt.hasNext()){
                if (!checkCustomStrategy1Matched((Map<String, Object>) tseIt.next())){
                    tseIt.remove();
                }
            }
            if (tseList.size() != 0){
                tseList.forEach(s -> tseStockNoList.add(s.get("stock_no").toString()));
                tseTotalVolumnList = tseDailyBaseInfoRepository.getRecentTotalVolumn(tseStockNoList, tseStockNoList.size() * 20);
            }
        }
        List<Map<String, Object>> otcList = otcDailyBaseInfoRepository.getCustomStrategy1(queryDate);
        List<String> otcStockNoList = new ArrayList<>();
        List<Map<String, Object>> otcTotalVolumnList = new ArrayList<>();
        if (otcList.size() != 0){
            Iterator otcIt = otcList.iterator();
            while (otcIt.hasNext()){
                if (!checkCustomStrategy1Matched((Map<String, Object>) otcIt.next())){
                    otcIt.remove();
                }
            }
            if (otcList.size() != 0){
                otcList.forEach(s -> otcStockNoList.add(s.get("stock_no").toString()));
                otcTotalVolumnList = otcDailyBaseInfoRepository.getRecentTotalVolumn(otcStockNoList, otcStockNoList.size() * 20);
            }
        }
        if (!( tseList.size() == 0 && otcList.size() == 0 )){
            resultList.addAll(tseList);
            resultList.addAll(otcList);
            tseTotalVolumnList.addAll(otcTotalVolumnList);
            tseStockNoList.addAll(otcStockNoList);
            for(Map<String, Object> map : tseTotalVolumnList){
                if (Integer.valueOf(map.get("sumVolumn").toString()) < 20000){
                    tseStockNoList.remove(map.get("stock_no").toString());
                }
            }
            Iterator resultIt = resultList.iterator();
            while (resultIt.hasNext()){
                Map<String, Object> temp = (Map<String, Object>) resultIt.next();
                if (!tseStockNoList.contains(temp.get("stock_no").toString())){
                    resultIt.remove();
                }
            }
        }
        //組訊息
        StringBuilder message = new StringBuilder();
        message.append(queryDate.toString() + "\r\n傑升客製策略一:  " + "\r\n");
        if (resultList.size() == 0){
            message.append("今日無符合標的!");
        }else{
            for(Map<String, Object> map : resultList){
                message.append(map.get("stock_name").toString());
                message.append(":");
                message.append(map.get("end_price").toString());
                message.append(" (");
                String diffPrice = map.get("diff_price").toString();
                if (Double.valueOf(diffPrice) >= 0){
                    diffPrice = "+" + diffPrice;
                }
                message.append(diffPrice);
                message.append(")\r\n");
            }
        }
        return message.toString();
    }

    private boolean checkCustomStrategy1Matched(Map<String, Object> priceInfo){
        double startPrice = Double.valueOf(priceInfo.get("start_price").toString());
        double endPrice = Double.valueOf(priceInfo.get("end_price").toString());
        double highPrice = Double.valueOf(priceInfo.get("high_price").toString());
        double lowPrice = Double.valueOf(priceInfo.get("low_price").toString());
        double kSolidRange = endPrice - startPrice;
        if (kSolidRange > ( ( highPrice - endPrice ) * 0.66 ) || ( startPrice - lowPrice ) > kSolidRange * 0.66){
            return false;
        }
        return true;
    }

    //找某特定日快要碰前高的股票
    public void getCustomStrategy2() throws Exception{
        //上市
        List<Map<String, Object>> tseList = tseDailyBaseInfoRepository.getTop3EndPriceByGroupStockNo();
        Map<String, Object> tempMap = new HashMap<String, Object>(600);
        for(Map<String, Object> map : tseList){
        }
        //上櫃
        List<Map<String, Object>> otcList = otcDailyBaseInfoRepository.getTop3EndPriceByGroupStockNo();
    }

    public List<Map<String, Object>> generateBuySellRankingReport(LocalDate startDate, LocalDate endDate, int queryType) throws Exception{
        List<Map<String, Object>> tseList = tseDailyBaseInfoRepository.getFiAndItBuySellStrategy(startDate, endDate);
        List<Map<String, Object>> otcList = otcDailyBaseInfoRepository.getFiAndItBuySellStrategy(startDate, endDate);
        //外投買排行
        if (queryType == 0 || queryType == 1){//產週報或組外投買訊息
            List<Map<String, Object>> buyResultList = new ArrayList<>();
            buyResultList.addAll(tseList.subList(0, 30));
            buyResultList.addAll(otcList.subList(0, 20));
            if (queryType == 1){
                return buyResultList;
            }
            String buyFilePath = reportService.generateWeeklyStrategyReport(weeklyBuyStrategy1, buyResultList);
            awsService.uploadToS3(buyFilePath, weeklyBuyStrategy1 + "_" + DateUtils.todayDate);
        }
        //外投賣排行
        if (queryType == 0 || queryType == 2){//產週報或組外投賣訊息
            Collections.reverse(tseList);
            Collections.reverse(otcList);
            List<Map<String, Object>> sellResultList = new ArrayList<>();
            sellResultList.addAll(tseList.subList(0, 30));
            sellResultList.addAll(otcList.subList(0, 20));
            if (queryType == 2){
                return sellResultList;
            }
            String sellFilePath = reportService.generateWeeklyStrategyReport(weeklySellStrategy1, sellResultList);
            awsService.uploadToS3(sellFilePath, weeklySellStrategy1 + "_" + DateUtils.todayDate);
        }
        //自營商避險買排行
        if (queryType == 0 || queryType == 3){//產週報或組自營商買避險訊息
            Collections.sort(tseList, dealerHedgingComparator);
            Collections.sort(otcList, dealerHedgingComparator);
            List<Map<String, Object>> dealerResultList = new ArrayList<>();
            dealerResultList.addAll(tseList.subList(0, 20));
            dealerResultList.addAll(otcList.subList(0, 10));
            if (queryType == 3){
                return dealerResultList;
            }
            String dealerHedgingFilePath = reportService.generateWeeklyStrategyReport(weeklyDealerStrategy1, dealerResultList);
            awsService.uploadToS3(dealerHedgingFilePath, weeklyDealerStrategy1 + "_" + DateUtils.todayDate);
            return null;
        }
        throw new Exception("invalid queryType!");
    }

    public String generateBullStrategyMessage(int strategyType, LocalDate queryDate) throws Exception{
        //多方策略
        StringBuilder bullResultList = new StringBuilder();
        List<Map<String, Object>> bullStrategyList = new ArrayList<Map<String, Object>>();
        switch(strategyType){
            case 1:
                List<Map<String, Object>> bull1TseList = tseDailyAvgInfoRepository.getTseBullStrategy1(queryDate);
                List<Map<String, Object>> bull1OtcList = otcDailyAvgInfoRepository.getOtcBullStrategy1(queryDate);
                bullStrategyList.addAll(bull1TseList);
                bullStrategyList.addAll(bull1OtcList);
                bullResultList.append(queryDate.toString() + "\r\n均線多排且外投買:  " + "\r\n");
                break;
            case 2:
                List<Map<String, Object>> bull2TseList = tseDailyAvgInfoRepository.getTseBullStrategy2(queryDate);
                List<Map<String, Object>> bull2OtcList = otcDailyAvgInfoRepository.getOtcBullStrategy2(queryDate);
                bullStrategyList.addAll(bull2TseList);
                bullStrategyList.addAll(bull2OtcList);
                bullResultList.append(queryDate.toString() + "\r\n收月季線上且外投買:  " + "\r\n");
                break;
        }
        for(Map<String, Object> map : bullStrategyList){
            bullResultList.append(map.get("股名").toString());
            bullResultList.append(":");
            bullResultList.append(map.get("收盤價").toString());
            bullResultList.append(" (");
            String diffPrice = map.get("漲跌點").toString();
            if (Double.valueOf(diffPrice) >= 0){
                diffPrice = "+" + diffPrice;
            }
            bullResultList.append(diffPrice);
            bullResultList.append(")\r\n");
            bullResultList.append("[");
            bullResultList.append(map.get("外資買賣超").toString());
            bullResultList.append(", ");
            bullResultList.append(map.get("投信買賣超").toString());
            bullResultList.append("]\r\n\r\n");
        }
        return bullResultList.toString();
    }

    public String generateBearStrategyMessage(int strategyType, LocalDate queryDate) throws Exception{
        //空方策略
        StringBuilder bearResultList = new StringBuilder();
        List<Map<String, Object>> bearStrategyList = new ArrayList<Map<String, Object>>();
        switch(strategyType){
            case 1:
                List<Map<String, Object>> bear1TseList = tseDailyAvgInfoRepository.getTseBearStrategy1(queryDate);
                List<Map<String, Object>> bear1OtcList = otcDailyAvgInfoRepository.getOtcBearStrategy1(queryDate);
                bearStrategyList.addAll(bear1TseList);
                bearStrategyList.addAll(bear1OtcList);
                bearResultList.append(queryDate.toString() + "\r\n均線下彎且外投賣:  \r\n");
                break;
            case 2:
                List<Map<String, Object>> bear2TseList = tseDailyAvgInfoRepository.getTseBearStrategy2(queryDate);
                List<Map<String, Object>> bear2OtcList = otcDailyAvgInfoRepository.getOtcBearStrategy2(queryDate);
                bearStrategyList.addAll(bear2TseList);
                bearStrategyList.addAll(bear2OtcList);
                bearResultList.append(queryDate.toString() + "\r\n收月季線下且外投賣:  \r\n");
                break;
        }
        for(Map<String, Object> map : bearStrategyList){
            bearResultList.append(map.get("股名").toString());
            bearResultList.append(":");
            bearResultList.append(map.get("收盤價").toString());
            bearResultList.append(" (");
            String diffPrice = map.get("漲跌點").toString();
            if (Double.valueOf(diffPrice) >= 0){
                diffPrice = "+" + diffPrice;
            }
            bearResultList.append(diffPrice);
            bearResultList.append(")\r\n");
            bearResultList.append("[");
            bearResultList.append(map.get("外資買賣超").toString());
            bearResultList.append(", ");
            bearResultList.append(map.get("投信買賣超").toString());
            bearResultList.append("]\r\n\r\n");
        }
        return bearResultList.toString();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void generateBearStrategyReport(int stockType, LocalDate date) throws Exception{
        List<Map<String, Object>> resultList = new ArrayList<>();
        switch(stockType){
            case 1://tse
                List<Map<String, Object>> tseResult = tseDailyAvgInfoRepository.getTseBearStrategy2(date);
                resultList.addAll(tseResult);
                break;
            case 2://otc
                List<Map<String, Object>> otcResult = otcDailyAvgInfoRepository.getOtcBearStrategy2(date);
                resultList.addAll(otcResult);
                break;
            default://全拿
                List<Map<String, Object>> tseList = tseDailyAvgInfoRepository.getTseBearStrategy2(date);
                List<Map<String, Object>> otcList = otcDailyAvgInfoRepository.getOtcBearStrategy2(date);
                resultList.addAll(tseList);
                resultList.addAll(otcList);
                break;
        }
        logger.info(gson.toJson(resultList.toString()));
        String filePath = reportService.generateStrategyReport(bearStrategy1, resultList, date);
        awsService.uploadToS3(filePath, bearStrategy1 + "_" + date.toString());
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void generateBullStrategyReport(int stockType, LocalDate date) throws Exception{
        List<Map<String, Object>> resultList = new ArrayList<>();
        switch(stockType){
            case 1://tse
                List<Map<String, Object>> tseResult = tseDailyAvgInfoRepository.getTseBullStrategy1(date);
                resultList.addAll(tseResult);
                break;
            case 2://otc
                List<Map<String, Object>> otcResult = otcDailyAvgInfoRepository.getOtcBullStrategy1(date);
                resultList.addAll(otcResult);
                break;
            default://全拿
                List<Map<String, Object>> tseList = tseDailyAvgInfoRepository.getTseBullStrategy1(date);
                List<Map<String, Object>> otcList = otcDailyAvgInfoRepository.getOtcBullStrategy1(date);
                resultList.addAll(tseList);
                resultList.addAll(otcList);
                break;
        }
        logger.info(gson.toJson(resultList.toString()));
        String filePath = reportService.generateStrategyReport(bullStrategy1, resultList, date);
        awsService.uploadToS3(filePath, bullStrategy1 + "_" + date.toString());
    }
}
