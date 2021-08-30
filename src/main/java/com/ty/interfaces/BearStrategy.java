package com.ty.interfaces;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.ty.enums.StockType;

public interface BearStrategy{

    List<Map<String, Object>> getBearStrategyInfoList(StockType stockType, LocalDate queryDate);
}
