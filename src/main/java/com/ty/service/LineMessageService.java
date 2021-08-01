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
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.linecorp.bot.model.message.Message;
import com.ty.Util.HttpUtils;
import com.ty.entity.LineUserAccount;
import com.ty.repository.LineUserAccountRepository;
import com.ty.vo.HttpResponseVo;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class LineMessageService{

    private static final Logger logger = LoggerFactory.getLogger(LineMessageService.class);

    @Value("{$line.push_message.url}")
    private String pusgMessageUrl;

    @Value("{$line.multicast.url}")
    private String multicastUrl;

    @Value("{$line.broadcast.url}")
    private String broadcastUrl;

    @Autowired
    LineUserAccountRepository lineUserAccountRepository;

    @Autowired
    HttpUtils httpUtils;

    public void parseUserInfo(String message) throws Exception{
        JSONObject json = new JSONObject(message);
        String destination = json.get("destination").toString();
        logger.info("destination: " + destination);
        JSONArray arr = json.getJSONArray("events");
        if (!arr.isEmpty()){
            JSONObject event = arr.getJSONObject(0);
            JSONObject source = (JSONObject) event.get("source");
            String uid = source.get("userId").toString();
            logger.info("line uid: " + uid);
            if (lineUserAccountRepository.findByLineUid(uid) == null){
                String response = httpUtils.getLineUserInfo(uid);
                JSONObject responseJson = new JSONObject(response);
                responseJson.get("displayName");
                LineUserAccount lineUserAccount = new LineUserAccount();
                lineUserAccount.setLineUid(uid);
                lineUserAccount.setName(responseJson.get("displayName").toString());
                lineUserAccount.setCreateTime(new Date());
                lineUserAccountRepository.save(lineUserAccount);
            }
        }else{
            throw new Exception("no events info!");
        }
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
