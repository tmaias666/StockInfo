package com.ty.strategyImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ty.Util.DateUtils;
import com.ty.enums.StockType;
import com.ty.interfaces.BullStrategy;
import com.ty.interfaces.RankingStartegy;
import com.ty.repository.OtcDailyAvgInfoRepository;
import com.ty.repository.OtcDailyBaseInfoRepository;
import com.ty.repository.TseDailyAvgInfoRepository;
import com.ty.repository.TseDailyBaseInfoRepository;

@Service
public class LegalPersonRankingImpl implements RankingStartegy{

    @Autowired
    private TseDailyBaseInfoRepository tseDailyBaseInfoRepository;

    @Autowired
    private OtcDailyBaseInfoRepository otcDailyBaseInfoRepository;

    @Override
    public List<Map<String, Object>> getBuyRankingInfo(LocalDate startDate, LocalDate endDate){
        //外投買排行
        List<Map<String, Object>> tseList = tseDailyBaseInfoRepository.getFiAndItBuySellStrategy(startDate, endDate);
        List<Map<String, Object>> otcList = otcDailyBaseInfoRepository.getFiAndItBuySellStrategy(startDate, endDate);
        List<Map<String, Object>> buyResultList = new ArrayList<>();
        buyResultList.addAll(tseList.subList(0, 30));
        buyResultList.addAll(otcList.subList(0, 20));
        return buyResultList;
    }

    @Override
    public List<Map<String, Object>> getSellRankingInfo(LocalDate startDate, LocalDate endDate){
        //外投賣排行
        List<Map<String, Object>> tseList = tseDailyBaseInfoRepository.getFiAndItBuySellStrategy(startDate, endDate);
        List<Map<String, Object>> otcList = otcDailyBaseInfoRepository.getFiAndItBuySellStrategy(startDate, endDate);
        Collections.reverse(tseList);
        Collections.reverse(otcList);
        List<Map<String, Object>> sellResultList = new ArrayList<>();
        sellResultList.addAll(tseList.subList(0, 30));
        sellResultList.addAll(otcList.subList(0, 20));
        return sellResultList;
    }
}
