package com.ty.strategyImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ty.interfaces.MessageGenerator;

@Service
public class StrategyMessageImpl implements MessageGenerator{

    @Override
    public String generateStrategyMessage(List<Map<String, Object>> strategyInfoList, LocalDate queryDate, String strategyName){
        StringBuilder messageResult = new StringBuilder();
        messageResult.append(queryDate.toString() + "\r\n" + strategyName + "\r\n");
        for(Map<String, Object> map : strategyInfoList){
            messageResult.append(map.get("股名").toString());
            messageResult.append(":");
            messageResult.append(map.get("收盤價").toString());
            messageResult.append(" (");
            String diffPrice = map.get("漲跌點").toString();
            if (Double.valueOf(diffPrice) >= 0){
                diffPrice = "+" + diffPrice;
            }
            messageResult.append(diffPrice);
            messageResult.append(")\r\n");
            messageResult.append("[");
            messageResult.append(map.get("外資買賣超").toString());
            messageResult.append(", ");
            messageResult.append(map.get("投信買賣超").toString());
            messageResult.append("]\r\n\r\n");
        }
        return messageResult.toString();
    }
}
