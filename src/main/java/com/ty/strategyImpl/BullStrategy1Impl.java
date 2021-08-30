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
public class BullStrategy1Impl implements BullStrategy{

    @Autowired
    private TseDailyAvgInfoRepository tseDailyAvgInfoRepository;

    @Autowired
    private OtcDailyAvgInfoRepository otcDailyAvgInfoRepository;

    public static final String strategyName = "均線多排上五日線且外投買";

    @Override
    public List<Map<String, Object>> getBullStrategyInfoList(StockType stockType, LocalDate queryDate){
        List<Map<String, Object>> bullStrategy1List = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> bull1TseList = tseDailyAvgInfoRepository.getTseBullStrategy1(queryDate);
        List<Map<String, Object>> bull1OtcList = otcDailyAvgInfoRepository.getOtcBullStrategy1(queryDate);
        bullStrategy1List.addAll(bull1TseList);
        bullStrategy1List.addAll(bull1OtcList);
        return bullStrategy1List;
    }
}
