package com.ty.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
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
import java.util.List;
import java.util.Map;

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

    //找某特定日快要碰前高的股票
    public void getCustomStrategy1() throws Exception{
        //上市
        List<Map<String, Object>> tseList = tseDailyBaseInfoRepository.getTop3EndPriceByGroupStockNo();
        Map<String, Object> tempMap = new HashMap<String, Object>(600);
        for(Map<String, Object> map : tseList){
        }
        //上櫃
        List<Map<String, Object>> otcList = otcDailyBaseInfoRepository.getTop3EndPriceByGroupStockNo();
    }

    @Scheduled(cron = "0 30 18 ? * SAT")
    public void generateWeeklyStrategyReport() throws Exception{
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.minusDays(6);
        List<Map<String, Object>> tseList = tseDailyBaseInfoRepository.getWeeklyFiAndItStrategy(startDate, now);
        List<Map<String, Object>> otcList = otcDailyBaseInfoRepository.getWeeklyFiAndItStrategy(startDate, now);
        List<Map<String, Object>> buyResultList = new ArrayList<>();
        List<Map<String, Object>> sellResultList = new ArrayList<>();
        List<Map<String, Object>> dealerResultList = new ArrayList<>();
        //外投買排行
        buyResultList.addAll(tseList.subList(0, 30));
        buyResultList.addAll(otcList.subList(0, 30));
        String buyFilePath = reportService.generateWeeklyStrategyReport(weeklyBuyStrategy1, buyResultList);
        awsService.uploadToS3(buyFilePath, weeklyBuyStrategy1 + "_" + DateUtils.todayDate);
        //外投賣排行
        Collections.reverse(tseList);
        Collections.reverse(otcList);
        sellResultList.addAll(tseList.subList(0, 30));
        sellResultList.addAll(otcList.subList(0, 30));
        String sellFilePath = reportService.generateWeeklyStrategyReport(weeklySellStrategy1, sellResultList);
        awsService.uploadToS3(sellFilePath, weeklySellStrategy1 + "_" + DateUtils.todayDate);
        //自營商避險買排行
        Collections.sort(tseList, dealerHedgingComparator);
        Collections.sort(otcList, dealerHedgingComparator);
        dealerResultList.addAll(tseList.subList(0, 30));
        dealerResultList.addAll(otcList.subList(0, 30));
        String dealerHedgingFilePath = reportService.generateWeeklyStrategyReport(weeklyDealerStrategy1, dealerResultList);
        awsService.uploadToS3(dealerHedgingFilePath, weeklyDealerStrategy1 + "_" + DateUtils.todayDate);
    }

    @Scheduled(cron = "0 0 20 * * MON-FRI")
    public void sendDailyStrategyResult() throws Exception{
        try{
            //多方
            StringBuilder bullResultList = new StringBuilder();
            List<Map<String, Object>> bullTseList = tseDailyAvgInfoRepository.getTseBullStrategy1(LocalDate.now());
            List<Map<String, Object>> bullOtcList = otcDailyAvgInfoRepository.getOtcBullStrategy1(LocalDate.now());
            bullTseList.addAll(bullOtcList);
            bullResultList.append(DateUtils.todayDate + "\r\n多排上五均且外投買:  \r\n");
            for(Map<String, Object> map : bullTseList){
                //bullResultList.append(map.get("股號").toString());
                bullResultList.append(map.get("股名").toString());
                bullResultList.append(":");
                bullResultList.append(map.get("收盤價").toString());
                bullResultList.append(" [");
                bullResultList.append(map.get("外資買賣超").toString());
                bullResultList.append(", ");
                bullResultList.append(map.get("投信買賣超").toString());
                bullResultList.append("]\r\n");
            }
            TextMessage bullMessage = new TextMessage(bullResultList.toString());
            //空方
            StringBuilder bearResultList = new StringBuilder();
            List<Map<String, Object>> bearTseList = tseDailyAvgInfoRepository.getTseBearStrategy1(LocalDate.now());
            List<Map<String, Object>> bearOtcList = otcDailyAvgInfoRepository.getOtcBearStrategy1(LocalDate.now());
            bearTseList.addAll(bearOtcList);
            bearResultList.append(DateUtils.todayDate + "\r\n收月季線下且外投賣:  \r\n");
            for(Map<String, Object> map : bearTseList){
                //bearResultList.append(map.get("股號").toString());
                bearResultList.append(map.get("股名").toString());
                bearResultList.append(":");
                bearResultList.append(map.get("收盤價").toString());
                bearResultList.append(" [");
                bearResultList.append(map.get("外資買賣超").toString());
                bearResultList.append(", ");
                bearResultList.append(map.get("投信買賣超").toString());
                bearResultList.append("]\r\n");
            }
            TextMessage bearMessage = new TextMessage(bearResultList.toString());
            List<Message> messages = new ArrayList<Message>();
            messages.add(bullMessage);
            messages.add(bearMessage);
            //PushMessage pushMessage = new PushMessage("U11a61b18499a22d2ab9c435e05cebc2a", messages);
            Broadcast broadcast = new Broadcast(messages);
            LineMessagingClient client = LineMessagingClient.builder(channelAccessToken).build();
            BotApiResponse botApiResponse = client.broadcast(broadcast).get();//.pushMessage(pushMessage).get();
            logger.info(gson.toJson(botApiResponse));
        }catch(Exception e){
            logger.error("push message error: ", e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void generateBearStrategyReport(int stockType) throws Exception{
        List<Map<String, Object>> resultList = new ArrayList<>();
        switch(stockType){
            case 1://tse
                List<Map<String, Object>> tseResult = tseDailyAvgInfoRepository.getTseBearStrategy1(LocalDate.now());
                resultList.addAll(tseResult);
                break;
            case 2://otc
                List<Map<String, Object>> otcResult = otcDailyAvgInfoRepository.getOtcBearStrategy1(LocalDate.now());
                resultList.addAll(otcResult);
                break;
            default://全拿
                List<Map<String, Object>> tseList = tseDailyAvgInfoRepository.getTseBearStrategy1(LocalDate.now());
                List<Map<String, Object>> otcList = otcDailyAvgInfoRepository.getOtcBearStrategy1(LocalDate.now());
                resultList.addAll(tseList);
                resultList.addAll(otcList);
                break;
        }
        logger.info(gson.toJson(resultList.toString()));
        String filePath = reportService.generateStrategyReport(bearStrategy1, resultList);
        awsService.uploadToS3(filePath, bearStrategy1 + "_" + DateUtils.todayDate);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void generateBullStrategyReport(int stockType) throws Exception{
        List<Map<String, Object>> resultList = new ArrayList<>();
        switch(stockType){
            case 1://tse
                List<Map<String, Object>> tseResult = tseDailyAvgInfoRepository.getTseBullStrategy1(LocalDate.now());
                resultList.addAll(tseResult);
                break;
            case 2://otc
                List<Map<String, Object>> otcResult = otcDailyAvgInfoRepository.getOtcBullStrategy1(LocalDate.now());
                resultList.addAll(otcResult);
                break;
            default://全拿
                List<Map<String, Object>> tseList = tseDailyAvgInfoRepository.getTseBullStrategy1(LocalDate.now());
                List<Map<String, Object>> otcList = otcDailyAvgInfoRepository.getOtcBullStrategy1(LocalDate.now());
                resultList.addAll(tseList);
                resultList.addAll(otcList);
                break;
        }
        logger.info(gson.toJson(resultList.toString()));
        String filePath = reportService.generateStrategyReport(bullStrategy1, resultList);
        awsService.uploadToS3(filePath, bullStrategy1 + "_" + DateUtils.todayDate);
    }
}
