package com.ty.context;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.ty.interfaces.MessageGenerator;

public class MessageContext{

    private MessageGenerator messageGenerator;

    public MessageContext(MessageGenerator messageGenerator){
        this.messageGenerator = messageGenerator;
    }

    public String getResultMessage(List<Map<String, Object>> strategyInfoList, LocalDate queryDate, String strategyName){
        return messageGenerator.generateStrategyMessage(strategyInfoList, queryDate, strategyName);
    }
}
