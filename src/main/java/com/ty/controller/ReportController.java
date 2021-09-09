package com.ty.controller;

import java.time.LocalDate;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ty.entity.LineUserAccount;
import com.ty.enums.StrategyType;
import com.ty.factory.BearStrategyReportFactory;
import com.ty.factory.BullStrategyReportFactory;
import com.ty.factory.StrategyReportGenerator;
import com.ty.mybatis.mapper.LineUserAccountMapper;

@RestController
@RequestMapping("/report")
public class ReportController{

    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

    @Autowired
    LineUserAccountMapper lineUserAccountMapper;

    @Autowired
    BullStrategyReportFactory bullStrategyReportFactory;

    @Autowired
    BearStrategyReportFactory bearStrategyReportFactory;

    @GetMapping("/generateDailyReport")
    public ResponseEntity<Object> generateDailyReport(@RequestParam("date") String date){
        try{
            LocalDate querydate = LocalDate.parse(date);
            StrategyReportGenerator bullReport = bullStrategyReportFactory;
            bullReport.getStrategyReport(querydate, StrategyType.BullStrategy1);
            bullReport.getStrategyReport(querydate, StrategyType.BullStrategy2);
            StrategyReportGenerator bearReport = bearStrategyReportFactory;
            bearReport.getStrategyReport(querydate, StrategyType.BearStrategy1);
            bearReport.getStrategyReport(querydate, StrategyType.BearStrategy2);
        }catch(Exception e){
            logger.error("error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error: " + e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).body("Generate daily report successfully!");
    }
}
