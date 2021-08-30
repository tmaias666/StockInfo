package com.ty.context;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ty.enums.StockType;
import com.ty.interfaces.BearStrategy;
import com.ty.interfaces.BullStrategy;
import com.ty.interfaces.RankingStartegy;

public class StrategyContext{

    private BearStrategy bearStrategy;

    private BullStrategy bullStrategy;

    private RankingStartegy rankingStartegy;

    public StrategyContext(BearStrategy bearStrategy){
        this.bearStrategy = bearStrategy;
    }

    public StrategyContext(BullStrategy bullStrategy){
        this.bullStrategy = bullStrategy;
    }

    public StrategyContext(RankingStartegy rankingStartegy){
        this.rankingStartegy = rankingStartegy;
    }

    public List<Map<String, Object>> executeBullStrategy(StockType stockType, LocalDate queryDate){
        return bullStrategy.getBullStrategyInfoList(stockType, queryDate);
    }

    public List<Map<String, Object>> executeBearStrategy(StockType stockType, LocalDate queryDate){
        return bearStrategy.getBearStrategyInfoList(stockType, queryDate);
    }

    public List<List<Map<String, Object>>> executeRankingStartegy(LocalDate startDate, LocalDate endDate){
        List<List<Map<String, Object>>> resultList = new ArrayList<>();
        resultList.add(rankingStartegy.getBuyRankingInfo(startDate, endDate));
        resultList.add(rankingStartegy.getSellRankingInfo(startDate, endDate));
        return resultList;
    }
}
