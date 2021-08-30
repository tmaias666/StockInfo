package com.ty.strategyImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ty.interfaces.MessageGenerator;

@Service
public class RankingMessageImpl implements MessageGenerator{

    @Override
    public String generateStrategyMessage(List<Map<String, Object>> rankingInfoList, LocalDate queryDate, String strategyName){
        StringBuilder resultMessage = new StringBuilder();
        resultMessage.append(queryDate.toString() + "\r\n" + strategyName + "\r\n");
        for(Map<String, Object> map : rankingInfoList){
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
}
