package com.ty.task;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ty.service.StockService;

@Service
public class FetchStockPriceInfo<T> implements Callable<T>{

    private StockService stockService;

    private int stockType;

    private List<String> dataSeries;

    private String accessToken;

    private String[] dateRange = new String[]{
        LocalDate.now().toString(), LocalDate.now().toString()
    };

    public FetchStockPriceInfo(){
    }

    public FetchStockPriceInfo(int stockType, List<String> dataSeries, String accessToken, StockService stockService){
        this.stockType = stockType;
        this.dataSeries = dataSeries;
        this.accessToken = accessToken;
        this.stockService = stockService;
    }

    @Override
    public T call() throws Exception{
        for(String stockInfo : dataSeries){
            String[] info = stockInfo.split(";");
            stockService.processAllTypeStockByFinmind(info[0], info[1], stockType, dateRange, accessToken);
        }
        return (T) "OK";
    }
}
