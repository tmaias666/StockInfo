package com.ty.factory;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.ty.enums.StrategyType;
import com.ty.facade.impl.JasonStrategyFacadeImpl;
import com.ty.interfaces.CustomStrategy;

public abstract class StrategyReportGenerator{

    public abstract List<Map<String, Object>> getDataList(LocalDate date, StrategyType type) throws Exception;

    public abstract void generateStrategyReport(LocalDate date, StrategyType type, List<Map<String, Object>> dataList) throws Exception;

    public final void getStrategyReport(LocalDate date, StrategyType type) throws Exception{
        List<Map<String, Object>> dataList = getDataList(date, type);
        generateStrategyReport(date, type, dataList);
    }
}
