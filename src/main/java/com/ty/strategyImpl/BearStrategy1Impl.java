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
public class BearStrategy1Impl implements BearStrategy{

    @Autowired
    private TseDailyAvgInfoRepository tseDailyAvgInfoRepository;

    @Autowired
    private OtcDailyAvgInfoRepository otcDailyAvgInfoRepository;
    
    public static final String strategyName = "5,10,20ma下彎且外投賣";

    @Override
    public List<Map<String, Object>> getBearStrategyInfoList(StockType stockType, LocalDate queryDate){
        List<Map<String, Object>> bearStrategy1List = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> bear1TseList = tseDailyAvgInfoRepository.getTseBearStrategy1(queryDate);
        List<Map<String, Object>> bear1OtcList = otcDailyAvgInfoRepository.getOtcBearStrategy1(queryDate);
        bearStrategy1List.addAll(bear1TseList);
        bearStrategy1List.addAll(bear1OtcList);
        return bearStrategy1List;
    }
}
