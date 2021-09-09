package com.ty.factory;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ty.enums.StockType;
import com.ty.enums.StrategyType;
import com.ty.service.ReportService;
import com.ty.strategyImpl.BullStrategy1Impl;
import com.ty.strategyImpl.BullStrategy2Impl;

@Service
public class BullStrategyReportFactory extends StrategyReportGenerator{

    @Autowired
    BullStrategy1Impl bullStrategy1Impl;

    @Autowired
    BullStrategy2Impl bullStrategy2Impl;

    @Autowired
    ReportService reportService;

    @Override
    public List<Map<String, Object>> getDataList(LocalDate date, StrategyType type) throws Exception{
        List<Map<String, Object>> resultList = null;
        if (StrategyType.BullStrategy1.equals(type)){
            resultList = bullStrategy1Impl.getBullStrategyInfoList(StockType.ALL, date);
        }else if (StrategyType.BullStrategy2.equals(type)){
            resultList = bullStrategy2Impl.getBullStrategyInfoList(StockType.ALL, date);
        }else{
            throw new Exception("invalid strategyType");
        }
        return resultList;
    }

    @Override
    public void generateStrategyReport(LocalDate date, StrategyType type, List<Map<String, Object>> dataList) throws Exception{
        if (StrategyType.BullStrategy1.equals(type)){
            reportService.generateStrategyReport(bullStrategy1Impl.strategyName, dataList, date);
        }else if (StrategyType.BullStrategy2.equals(type)){
            reportService.generateStrategyReport(bullStrategy2Impl.strategyName, dataList, date);
        }
    }
}
