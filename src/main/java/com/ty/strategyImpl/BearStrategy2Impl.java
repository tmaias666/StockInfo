package com.ty.strategyImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ty.enums.StockType;
import com.ty.interfaces.BearStrategy;
import com.ty.repository.OtcDailyAvgInfoRepository;
import com.ty.repository.TseDailyAvgInfoRepository;

@Service
public class BearStrategy2Impl implements BearStrategy{

    @Autowired
    private TseDailyAvgInfoRepository tseDailyAvgInfoRepository;

    @Autowired
    private OtcDailyAvgInfoRepository otcDailyAvgInfoRepository;
    
    public static final String strategyName = "收月季線下且外投賣";

    @Override
    public List<Map<String, Object>> getBearStrategyInfoList(StockType stockType, LocalDate queryDate){
        List<Map<String, Object>> bearStrategy2List = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> bear2TseList = tseDailyAvgInfoRepository.getTseBearStrategy2(queryDate);
        List<Map<String, Object>> bear2OtcList = otcDailyAvgInfoRepository.getOtcBearStrategy2(queryDate);
        bearStrategy2List.addAll(bear2TseList);
        bearStrategy2List.addAll(bear2OtcList);
        return bearStrategy2List;
    }
}
