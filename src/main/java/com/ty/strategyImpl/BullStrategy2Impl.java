package com.ty.strategyImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ty.enums.StockType;
import com.ty.interfaces.BullStrategy;
import com.ty.repository.OtcDailyAvgInfoRepository;
import com.ty.repository.TseDailyAvgInfoRepository;

@Service
public class BullStrategy2Impl implements BullStrategy{

    @Autowired
    private TseDailyAvgInfoRepository tseDailyAvgInfoRepository;

    @Autowired
    private OtcDailyAvgInfoRepository otcDailyAvgInfoRepository;

    public static final String strategyName = "收月季線上且外投買";

    @Override
    public List<Map<String, Object>> getBullStrategyInfoList(StockType stockType, LocalDate queryDate){
        List<Map<String, Object>> bullStrategy2List = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> bull2TseList = tseDailyAvgInfoRepository.getTseBullStrategy2(queryDate);
        List<Map<String, Object>> bull2OtcList = otcDailyAvgInfoRepository.getOtcBullStrategy2(queryDate);
        bullStrategy2List.addAll(bull2TseList);
        bullStrategy2List.addAll(bull2OtcList);
        return bullStrategy2List;
    }
}
