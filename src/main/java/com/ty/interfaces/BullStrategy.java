package com.ty.interfaces;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.ty.enums.StockType;

public interface BullStrategy{

    List<Map<String, Object>> getBullStrategyInfoList(StockType stockType, LocalDate queryDate);
}
