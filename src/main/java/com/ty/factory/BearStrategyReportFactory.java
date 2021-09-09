package com.ty.factory;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ty.enums.StockType;
import com.ty.enums.StrategyType;
import com.ty.service.ReportService;
import com.ty.strategyImpl.BearStrategy1Impl;
import com.ty.strategyImpl.BearStrategy2Impl;

@Service
public class BearStrategyReportFactory extends StrategyReportGenerator{

    @Autowired
    BearStrategy1Impl bearStrategy1Impl;

    @Autowired
    BearStrategy2Impl bearStrategy2Impl;

    @Autowired
    ReportService reportService;

    @Override
    public List<Map<String, Object>> getDataList(LocalDate date, StrategyType type) throws Exception{
        List<Map<String, Object>> resultList = null;
        if (StrategyType.BearStrategy1.equals(type)){
            resultList = bearStrategy1Impl.getBearStrategyInfoList(StockType.ALL, date);
        }else if (StrategyType.BearStrategy2.equals(type)){
            resultList = bearStrategy2Impl.getBearStrategyInfoList(StockType.ALL, date);
        }else{
            throw new Exception("invalid strategyType");
        }
        return resultList;
    }

    @Override
    public void generateStrategyReport(LocalDate date, StrategyType type, List<Map<String, Object>> dataList) throws Exception{
        if (StrategyType.BearStrategy1.equals(type)){
            reportService.generateStrategyReport(bearStrategy1Impl.strategyName, dataList, date);
        }else if (StrategyType.BearStrategy2.equals(type)){
            reportService.generateStrategyReport(bearStrategy2Impl.strategyName, dataList, date);
        }
    }
}
