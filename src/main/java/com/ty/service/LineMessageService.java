package com.ty.service;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.Broadcast;
import com.linecorp.bot.model.Multicast;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.response.BotApiResponse;
import com.ty.Util.DateUtils;
import com.ty.Util.HttpUtils;
import com.ty.config.LineConfig;
import com.ty.entity.LineUserAccount;
import com.ty.repository.LineUserAccountRepository;
import com.ty.repository.OtcDailyAvgInfoRepository;
import com.ty.repository.OtcDailyLegalInfoRepository;
import com.ty.repository.TseDailyAvgInfoRepository;
import com.ty.vo.HttpResponseVo;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LineMessageService{

    private static final Logger logger = LoggerFactory.getLogger(LineMessageService.class);

    @Value("{$line.push_message.url}")
    private String pusgMessageUrl;

    @Value("{$line.multicast.url}")
    private String multicastUrl;

    @Value("{$line.broadcast.url}")
    private String broadcastUrl;

    private static final Gson gson = new Gson();

    private static final String requestAuthMessage = "請聯絡開發者開通您的權限\r\n才可使用相關功能!";

    private static final String grantedAuthMessage = "已開通權限請確認!";

    @Autowired
    StockStrategyService stockStrategyService;

    @Autowired
    LineUserAccountRepository lineUserAccountRepository;

    @Autowired
    OtcDailyLegalInfoRepository otcDailyLegalInfoRepository;

    @Autowired
    HttpUtils httpUtils;

    @Autowired
    Cache<String, Object> caffeineCache;

    @Autowired
    LineConfig lineConfig;

    public void parseUserInfoAndReply(String message) throws Exception{
        JSONObject json = new JSONObject(message);
        String destination = json.get("destination").toString();
        logger.info("destination: " + destination);
        JSONArray arr = json.getJSONArray("events");
        if (!arr.isEmpty()){
            JSONObject event = arr.getJSONObject(0);
            JSONObject source = (JSONObject) event.get("source");
            String uid = source.get("userId").toString();
            logger.info("line uid: " + uid);
            LineUserAccount existUser = lineUserAccountRepository.findByLineUid(uid);
            if (existUser == null){
                String response = httpUtils.getLineUserInfo(uid);
                logger.info("getLineUserInfo response: " + response);
                JSONObject responseJson = new JSONObject(response);
                LineUserAccount lineUserAccount = new LineUserAccount();
                lineUserAccount.setLineUid(uid);
                lineUserAccount.setName(filterEmoji(responseJson.get("displayName").toString()));
                lineUserAccount.setCreateTime(new Date());
                lineUserAccount.setIsVerified(0);
                existUser = lineUserAccountRepository.save(lineUserAccount);
            }
            if ("message".equals(event.get("type").toString()) && event.get("replyToken") != null){
                JSONObject messageObj = (JSONObject) event.get("message");
                String text = messageObj.get("text").toString();
                String replyToken = event.get("replyToken").toString();
                logger.info("received text: " + text + ";replyToken: " + replyToken);
                //可審核權限
                if (!grantAuthority(existUser, text, replyToken)){
                    processKeywordAndReply(text, replyToken, existUser.getIsVerified());
                }
            }
        }else{
            throw new Exception("no events info!");
        }
    }

    public void processKeywordAndReply(String message, String replyToken, int isVerified){
        try{
            if (isVerified == 0){
                ReplyMessage replyMessage = new ReplyMessage(replyToken, new TextMessage(requestAuthMessage));
                BotApiResponse botApiResponse = lineConfig.lineMessagingClient().replyMessage(replyMessage).get();
                logger.info(gson.toJson(botApiResponse));
                return;
            }
            String responseMessage = "";
            //處理撈最近一日資料邏輯
            LocalDate queryDate = LocalDate.parse(otcDailyLegalInfoRepository.getLatestSyncDate(LocalDate.now()));
            List<Message> msgList = new ArrayList<Message>();
            switch(message){
                case "近一日均線多排外投買":
                    if (caffeineCache.getIfPresent("latestBullStrategy1" + queryDate.toString()) != null){
                        responseMessage = caffeineCache.getIfPresent("latestBullStrategy1" + queryDate.toString()).toString();
                    }else{
                        responseMessage = stockStrategyService.generateBullStrategyMessage(1, queryDate);
                        caffeineCache.put("latestBullStrategy1" + queryDate.toString(), responseMessage);
                    }
                    msgList.add(new TextMessage(responseMessage));
                    break;
                case "近一日月季線上外投買":
                    if (caffeineCache.getIfPresent("latestBullStrategy2" + queryDate.toString()) != null){
                        responseMessage = caffeineCache.getIfPresent("latestBullStrategy2" + queryDate.toString()).toString();
                    }else{
                        responseMessage = stockStrategyService.generateBullStrategyMessage(2, queryDate);
                        caffeineCache.put("latestBullStrategy2" + queryDate.toString(), responseMessage);
                    }
                    msgList.add(new TextMessage(responseMessage));
                    break;
                case "近一日均線下彎外投賣":
                    if (caffeineCache.getIfPresent("latestBearStrategy1" + queryDate.toString()) != null){
                        responseMessage = caffeineCache.getIfPresent("latestBearStrategy1" + queryDate.toString()).toString();
                    }else{
                        responseMessage = stockStrategyService.generateBearStrategyMessage(1, queryDate);
                        caffeineCache.put("latestBearStrategy1" + queryDate.toString(), responseMessage);
                    }
                    msgList.add(new TextMessage(responseMessage));
                    break;
                case "近一日月季線下外投賣":
                    if (caffeineCache.getIfPresent("latestBearStrategy2" + queryDate.toString()) != null){
                        responseMessage = caffeineCache.getIfPresent("latestBearStrategy2" + queryDate.toString()).toString();
                    }else{
                        responseMessage = stockStrategyService.generateBearStrategyMessage(2, queryDate);
                        caffeineCache.put("latestBearStrategy2" + queryDate.toString(), responseMessage);
                    }
                    msgList.add(new TextMessage(responseMessage));
                    break;
                case "查詢週報統計資訊":
                    String weekRank1Message;
                    String weekRank2Message;
                    String weekRank3Message;
                    LocalDate startDate = LocalDate.now();
                    LocalDate endDate = LocalDate.now();
                    int dayOfWeek = LocalDate.now().getDayOfWeek().getValue();
                    if (dayOfWeek == 7){
                        startDate = startDate.minusDays(7);
                        endDate = endDate.minusDays(1);
                    }else if (dayOfWeek == 6){
                        startDate = startDate.minusDays(6);
                    }else{
                        startDate = startDate.minusDays(dayOfWeek + 7);
                        endDate = endDate.minusDays(dayOfWeek + 1);
                    }
                    if (caffeineCache.getIfPresent("weekRank1List" + endDate.toString()) != null && caffeineCache.getIfPresent("weekRank2List" + endDate.toString()) != null && caffeineCache.getIfPresent("weekRank3List" + endDate.toString()) != null){
                        weekRank1Message = caffeineCache.getIfPresent("weekRank1List" + endDate.toString()).toString();
                        weekRank2Message = caffeineCache.getIfPresent("weekRank2List" + endDate.toString()).toString();
                        weekRank3Message = caffeineCache.getIfPresent("weekRank3List" + endDate.toString()).toString();
                    }else{
                        List<Map<String, Object>> weekRank1List = stockStrategyService.generateBuySellRankingReport(startDate, endDate, 1);
                        List<Map<String, Object>> weekRank2List = stockStrategyService.generateBuySellRankingReport(startDate, endDate, 2);
                        List<Map<String, Object>> weekRank3List = stockStrategyService.generateBuySellRankingReport(startDate, endDate, 3);
                        weekRank1Message = generateRankingMessage(endDate, weekRank1List, "近一周外投買超總和排行");
                        weekRank2Message = generateRankingMessage(endDate, weekRank2List, "近一周外投賣超總和排行");
                        weekRank3Message = generateRankingMessage(endDate, weekRank3List, "近一周自營商避險買超排行");
                        caffeineCache.put("weekRank1List" + endDate.toString(), weekRank1Message);
                        caffeineCache.put("weekRank2List" + endDate.toString(), weekRank2Message);
                        caffeineCache.put("weekRank3List" + endDate.toString(), weekRank3Message);
                    }
                    msgList.add(new TextMessage(weekRank1Message));
                    msgList.add(new TextMessage(weekRank2Message));
                    msgList.add(new TextMessage(weekRank3Message));
                    break;
                case "近一日外投買賣排行":
                    String dailyRankList1Message;
                    String dailyRankList2Message;
                    //外投買
                    if (caffeineCache.getIfPresent("dailyRankList1" + queryDate.toString()) != null){
                        dailyRankList1Message = caffeineCache.getIfPresent("dailyRankList1" + queryDate.toString()).toString();
                    }else{
                        List<Map<String, Object>> rank1List = stockStrategyService.generateBuySellRankingReport(queryDate.minusDays(1), queryDate.plusDays(1), 1);
                        dailyRankList1Message = generateRankingMessage(queryDate, rank1List, "最近一日外投買排行");
                        caffeineCache.put("dailyRankList1" + queryDate.toString(), dailyRankList1Message);
                    }
                    msgList.add(new TextMessage(dailyRankList1Message));
                    //外投賣
                    if (caffeineCache.getIfPresent("dailyRankList2" + queryDate.toString()) != null){
                        dailyRankList2Message = caffeineCache.getIfPresent("dailyRankList2" + queryDate.toString()).toString();
                    }else{
                        List<Map<String, Object>> rank2List = stockStrategyService.generateBuySellRankingReport(queryDate.minusDays(1), queryDate.plusDays(1), 2);
                        dailyRankList2Message = generateRankingMessage(queryDate, rank2List, "最近一日外投賣排行");
                        caffeineCache.put("dailyRankList2" + queryDate.toString(), dailyRankList2Message);
                    }
                    msgList.add(new TextMessage(dailyRankList2Message));
                    break;
                case "最近一日自營商避險買":
                    if (caffeineCache.getIfPresent("dailyRankList3" + queryDate.toString()) != null){
                        responseMessage = caffeineCache.getIfPresent("dailyRankList3" + queryDate.toString()).toString();
                    }else{
                        List<Map<String, Object>> rank3List = stockStrategyService.generateBuySellRankingReport(queryDate.minusDays(1), queryDate.plusDays(1), 3);
                        responseMessage = generateRankingMessage(queryDate, rank3List, "最近一日自營商避險買");
                        caffeineCache.put("dailyRankList3" + queryDate.toString(), responseMessage);
                    }
                    msgList.add(new TextMessage(responseMessage));
                    break;
                default:
                    responseMessage = "訊息錯誤請重試!";
                    msgList.add(new TextMessage(responseMessage));
                    break;
            }
            ReplyMessage replyMessage = new ReplyMessage(replyToken, msgList);
            BotApiResponse botApiResponse = lineConfig.lineMessagingClient().replyMessage(replyMessage).get();
            logger.info(gson.toJson(botApiResponse));
        }catch(Exception e){
            logger.error("process error: ", e);
        }
    }

    //@Scheduled(cron = "0 22 18 * * MON-FRI")
    public void sendDailyStrategyResultMessage() throws Exception{
        try{
            LocalDate queryDate = LocalDate.now();
            List<Message> messages = new ArrayList<Message>();
            TextMessage bullMessage = new TextMessage(stockStrategyService.generateBullStrategyMessage(1, queryDate));
            TextMessage bearMessage = new TextMessage(stockStrategyService.generateBearStrategyMessage(2, queryDate));
            List<Map<String, Object>> rank1List = stockStrategyService.generateBuySellRankingReport(queryDate.minusDays(1), queryDate.plusDays(1), 1);
            TextMessage rank1ListMessage = new TextMessage(generateRankingMessage(queryDate, rank1List, "當日外投買排行"));
            List<Map<String, Object>> rank2List = stockStrategyService.generateBuySellRankingReport(queryDate.minusDays(1), queryDate.plusDays(1), 2);
            TextMessage rank2ListMessage = new TextMessage(generateRankingMessage(queryDate, rank2List, "當日外投賣排行"));
            List<Map<String, Object>> rank3List = stockStrategyService.generateBuySellRankingReport(queryDate.minusDays(1), queryDate.plusDays(1), 3);
            TextMessage rank3ListMessage = new TextMessage(generateRankingMessage(queryDate, rank3List, "當日自營商避險買"));
            messages.add(bullMessage);
            messages.add(bearMessage);
            messages.add(rank1ListMessage);
            messages.add(rank2ListMessage);
            messages.add(rank3ListMessage);
            logger.info("Send daily strategy reports: " + gson.toJson(messages));
            //Set<String> uidList = new HashSet<>();
            //uidList.add("U91abff48f7a3f7dbd9b63b014bfdcf69");
            //uidList.add("U11a61b18499a22d2ab9c435e05cebc2a");
            //Multicast multicast = new Multicast(uidList, messages);
            //PushMessage pushMessage = new PushMessage("U11a61b18499a22d2ab9c435e05cebc2a", messages);
            Broadcast broadcast = new Broadcast(messages);
            //BotApiResponse botApiResponse = client.pushMessage(pushMessage).get();
            BotApiResponse botApiResponse = lineConfig.lineMessagingClient().broadcast(broadcast).get();
            //BotApiResponse botApiResponse = client.multicast(multicast).get();
            logger.info("send response: " + gson.toJson(botApiResponse));
        }catch(Exception e){
            logger.error("send message error: ", e);
        }
    }

    private String generateRankingMessage(LocalDate queryDate, List<Map<String, Object>> infoList, String strategyName){
        StringBuilder resultMessage = new StringBuilder();
        resultMessage.append(queryDate.toString() + "\r\n" + strategyName + "\r\n");
        for(Map<String, Object> map : infoList){
            resultMessage.append(map.get("stock_name").toString());
            //resultMessage.append(":");
            //resultMessage.append(map.get("end_price").toString());
            resultMessage.append("\r\n[");
            resultMessage.append(map.get("fi").toString());
            resultMessage.append(", ");
            resultMessage.append(map.get("it").toString());
            resultMessage.append(", ");
            resultMessage.append(map.get("ds").toString());
            resultMessage.append(", ");
            resultMessage.append(map.get("dh").toString());
            resultMessage.append("]\r\n\r\n");
        }
        return resultMessage.toString();
    }

    private boolean grantAuthority(LineUserAccount userAccount, String text, String replyToken) throws InterruptedException, ExecutionException{
        if (userAccount.getId() == 1 && text.equals("grantAuth")){
            List<LineUserAccount> userList = lineUserAccountRepository.findByIsVerified(0);
            for(LineUserAccount user : userList){
                user.setIsVerified(1);
            }
            lineUserAccountRepository.saveAll(userList);
            ReplyMessage replyMessage = new ReplyMessage(replyToken, new TextMessage(grantedAuthMessage));
            BotApiResponse botApiResponse = lineConfig.lineMessagingClient().replyMessage(replyMessage).get();
            logger.info(gson.toJson(botApiResponse));
            return true;
        }
        return false;
    }

    private String filterEmoji(String str){
        if (str.trim().isEmpty()){
            return str;
        }
        str = str.replaceAll("[^(a-zA-Z0-9\\u4e00-\\u9fa5)]", "");
        return str;
    }

    public void pushMessageService(List<Message> messageList) throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        String initialPushMsg = mapper.writeValueAsString(messageList);
        logger.info("initial pushMsg jsonString: " + initialPushMsg);
        //String targetUids = subscriptionUids.stream().map(p -> p).collect(Collectors.joining(","));
        String targetUids = "";
        String modifiedPushMsg = convertJsonString(messageList);
        logger.info("modified pushMsg jsonString: " + modifiedPushMsg + ", targetUids: " + targetUids);
        //multicast to line
        String multicastResult = multicastMsgToLine(modifiedPushMsg, targetUids);
        if ("200".equals(multicastResult)){
        }else{
            throw new Exception("呼叫line發送推播失敗: " + multicastResult);
        }
    }

    private String multicastMsgToLine(String lineMessageBody, String uid){
        String result = "";
        try{
            List<Header> headers = Arrays.asList(new BasicHeader("Content-type", "application/json;charset=UTF-8"));
            HttpEntity entity = new StringEntity(lineMessageBody, StandardCharsets.UTF_8.displayName());
            logger.info("multicastMsgToLine body: {}", lineMessageBody);
            HttpResponseVo rsp = HttpUtils.post(multicastUrl, headers.toArray(new Header[headers.size()]), entity);
            if (rsp.getStatus() != 200){
                logger.error("multicastMsgToLine fail: {}", rsp.getResponseBody());
                result = rsp.getResponseBody();
            }else{
                result = rsp.getStatus().toString();
            }
        }catch(Exception e){
            logger.error("multicastMsgToLine error: {}", e);
            result = e.getMessage();
        }
        return result;
    }

    private String convertJsonString(List<Message> msgList) throws JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        JsonArray msArray = new JsonArray();
        mapper.registerSubtypes(Message.class);
        for(Message msg : msgList){
            logger.info("convertJsonString msg: {}", mapper.writeValueAsString(msg));
            msArray.add(mapper.writeValueAsString(msg));
        }
        return msArray.toString();
    }
}
