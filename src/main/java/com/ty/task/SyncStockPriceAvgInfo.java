package com.ty.task;

import java.util.List;
import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ty.service.StockService;

@Service
public class SyncStockPriceAvgInfo<T> implements Callable<T>{

    private StockService stockService;

    private int stockType;

    private String[] dataSeries;

    public SyncStockPriceAvgInfo(){
    }

    public SyncStockPriceAvgInfo(int stockType, String[] dataSeries, StockService stockService){
        this.stockType = stockType;
        this.dataSeries = dataSeries;
        this.stockService = stockService;
    }

    @Override
    public T call() throws Exception{
        //        switch(stockType){
        //            case 1:
        //                stockService.syncStockPriceAvgInfo(dataSeries, stockType);
        //                break;
        //            case 2:
        //                stockService.syncStockPriceAvgInfo(dataSeries, stockType);
        //                break;
        //            default:
        //                throw new Exception("invalid stock type!");
        //        }
        stockService.syncStockPriceAvgInfo(dataSeries, stockType);
        return (T) "OK";
    }
}
