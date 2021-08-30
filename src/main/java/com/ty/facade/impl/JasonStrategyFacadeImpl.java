package com.ty.facade.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ty.facade.CustomStrategyFacade;
import com.ty.interfaces.CustomStrategy;
import com.ty.repository.OtcDailyBaseInfoRepository;
import com.ty.repository.TseDailyBaseInfoRepository;

@Service
public class JasonStrategyFacadeImpl implements CustomStrategy{

    @Autowired
    private TseDailyBaseInfoRepository tseDailyBaseInfoRepository;

    @Autowired
    private OtcDailyBaseInfoRepository otcDailyBaseInfoRepository;

    @Override
    public String getCustomStrategyMessage(LocalDate queryDate){
        //紅k,實體k棒小於上影線的2/3,下影線小於實體k的2/3,K棒整體在5均上,20日均量1000以上
        List<Map<String, Object>> resultList = new LinkedList<Map<String, Object>>();
        List<String> tseStockNoList = new ArrayList<>();
        List<Map<String, Object>> tseTotalVolumnList = new ArrayList<>();
        List<Map<String, Object>> tseList = tseDailyBaseInfoRepository.getCustomStrategy1(queryDate);
        if (tseList.size() != 0){
            Iterator tseIt = tseList.iterator();
            while (tseIt.hasNext()){
                if (!checkCustomStrategy1Matched((Map<String, Object>) tseIt.next())){
                    tseIt.remove();
                }
            }
            if (tseList.size() != 0){
                tseList.forEach(s -> tseStockNoList.add(s.get("stock_no").toString()));
                tseTotalVolumnList = tseDailyBaseInfoRepository.getRecentTotalVolumn(tseStockNoList, tseStockNoList.size() * 20);
            }
        }
        List<Map<String, Object>> otcList = otcDailyBaseInfoRepository.getCustomStrategy1(queryDate);
        List<String> otcStockNoList = new ArrayList<>();
        List<Map<String, Object>> otcTotalVolumnList = new ArrayList<>();
        if (otcList.size() != 0){
            Iterator otcIt = otcList.iterator();
            while (otcIt.hasNext()){
                if (!checkCustomStrategy1Matched((Map<String, Object>) otcIt.next())){
                    otcIt.remove();
                }
            }
            if (otcList.size() != 0){
                otcList.forEach(s -> otcStockNoList.add(s.get("stock_no").toString()));
                otcTotalVolumnList = otcDailyBaseInfoRepository.getRecentTotalVolumn(otcStockNoList, otcStockNoList.size() * 20);
            }
        }
        if (!( tseList.size() == 0 && otcList.size() == 0 )){
            resultList.addAll(tseList);
            resultList.addAll(otcList);
            tseTotalVolumnList.addAll(otcTotalVolumnList);
            tseStockNoList.addAll(otcStockNoList);
            for(Map<String, Object> map : tseTotalVolumnList){
                if (Integer.valueOf(map.get("sumVolumn").toString()) < 20000){
                    tseStockNoList.remove(map.get("stock_no").toString());
                }
            }
            Iterator resultIt = resultList.iterator();
            while (resultIt.hasNext()){
                Map<String, Object> temp = (Map<String, Object>) resultIt.next();
                if (!tseStockNoList.contains(temp.get("stock_no").toString())){
                    resultIt.remove();
                }
            }
        }
        //組訊息
        StringBuilder message = new StringBuilder();
        message.append(queryDate.toString() + "\r\n傑升客製策略一:  " + "\r\n");
        if (resultList.size() == 0){
            message.append("今日無符合標的!");
        }else{
            for(Map<String, Object> map : resultList){
                message.append(map.get("stock_name").toString());
                message.append(":");
                message.append(map.get("end_price").toString());
                message.append(" (");
                String diffPrice = map.get("diff_price").toString();
                if (Double.valueOf(diffPrice) >= 0){
                    diffPrice = "+" + diffPrice;
                }
                message.append(diffPrice);
                message.append(")\r\n");
            }
        }
        return message.toString();
    }

    private boolean checkCustomStrategy1Matched(Map<String, Object> priceInfo){
        double startPrice = Double.valueOf(priceInfo.get("start_price").toString());
        double endPrice = Double.valueOf(priceInfo.get("end_price").toString());
        double highPrice = Double.valueOf(priceInfo.get("high_price").toString());
        double lowPrice = Double.valueOf(priceInfo.get("low_price").toString());
        double kSolidRange = endPrice - startPrice;
        if (kSolidRange > ( ( highPrice - endPrice ) * 0.66 ) || ( startPrice - lowPrice ) > kSolidRange * 0.66){
            return false;
        }
        return true;
    }
}
