package com.ty.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.ty.repository.OtcDailyAvgInfoRepository;
import com.ty.repository.OtcDailyBaseInfoRepository;
import com.ty.repository.OtcDailyLegalInfoRepository;
import com.ty.repository.TseDailyAvgInfoRepository;
import com.ty.repository.TseDailyBaseInfoRepository;
import com.ty.repository.TseDailyLegalInfoRepository;
import com.ty.repository.StockMainRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class StockStrategyService{

    private static final Logger logger = LoggerFactory.getLogger(StockStrategyService.class);

    @Autowired
    private StockMainRepository stockMainRepository;

    @Autowired
    private TseDailyBaseInfoRepository tseDailyBaseInfoRepository;

    @Autowired
    private OtcDailyBaseInfoRepository otcDailyBaseInfoRepository;

    @Autowired
    private TseDailyAvgInfoRepository tseDailyAvgInfoRepository;

    @Autowired
    private OtcDailyAvgInfoRepository otcDailyAvgInfoRepository;

    @Autowired
    private TseDailyLegalInfoRepository tseDailyLegalInfoRepository;

    @Autowired
    private OtcDailyLegalInfoRepository otcDailyLegalInfoRepository;

    @Autowired
    ReportService reportService;

    private static final Gson gson = new Gson();

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void generateBearStrategyReport(int stockType) throws Exception{
        List<Map<String, Object>> resultList = new ArrayList<>();
        switch(stockType){
            case 1://tse
                List<Map<String, Object>> tseResult = tseDailyAvgInfoRepository.getTseBearStrategy1(LocalDate.now());
                resultList.addAll(tseResult);
                break;
            case 2://otc
                List<Map<String, Object>> otcResult = otcDailyAvgInfoRepository.getOtcBearStrategy1(LocalDate.now());
                resultList.addAll(otcResult);
                break;
            default://全拿
                List<Map<String, Object>> tseList = tseDailyAvgInfoRepository.getTseBearStrategy1(LocalDate.now());
                List<Map<String, Object>> otcList = otcDailyAvgInfoRepository.getOtcBearStrategy1(LocalDate.now());
                resultList.addAll(tseList);
                resultList.addAll(otcList);
                break;
        }
        logger.info(gson.toJson(resultList.toString()));
        reportService.generateStrategyReport("收在月季線下且外資投信賣超", resultList);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void generateBullStrategyReport(int stockType) throws Exception{
        List<Map<String, Object>> resultList = new ArrayList<>();
        switch(stockType){
            case 1://tse
                List<Map<String, Object>> tseResult = tseDailyAvgInfoRepository.getTseBullStrategy1(LocalDate.now());
                resultList.addAll(resultList);
                break;
            case 2://otc
                List<Map<String, Object>> otcResult = otcDailyAvgInfoRepository.getOtcBullStrategy1(LocalDate.now());
                resultList.addAll(otcResult);
                break;
            default://全拿
                List<Map<String, Object>> tseList = tseDailyAvgInfoRepository.getTseBullStrategy1(LocalDate.now());
                List<Map<String, Object>> otcList = otcDailyAvgInfoRepository.getOtcBullStrategy1(LocalDate.now());
                resultList.addAll(tseList);
                resultList.addAll(otcList);
                break;
        }
        logger.info(gson.toJson(resultList.toString()));
        reportService.generateStrategyReport("均線多排收上5均且外資投信買超", resultList);
    }
}
