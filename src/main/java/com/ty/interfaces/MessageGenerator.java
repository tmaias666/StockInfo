package com.ty.interfaces;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface MessageGenerator{

    String generateStrategyMessage(List<Map<String, Object>> strategyInfoList, LocalDate queryDate, String strategyName);
}
