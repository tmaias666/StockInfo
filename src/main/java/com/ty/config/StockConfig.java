package com.ty.config;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StockConfig{

    @Value("${stock_with_futures_tse}")
    String tseStockWithFutures;

    @Value("${stock_with_futures_otc}")
    String otcStockWithFutures;

    @Value("${stock_without_futures_tse}")
    String tseStockWithoutFutures;

    @Value("${stock_without_futures_otc}")
    String otcStockWithoutFutures;

    @Bean
    public String[] tseDataSeries(){
        String[] tseWithFuturesData = tseStockWithFutures.split(",");
        String[] tseWithoutFuturesData = tseStockWithoutFutures.split(",");
        return (String[]) ArrayUtils.addAll(tseWithFuturesData, tseWithoutFuturesData);
    }

    @Bean
    public String[] otcDataSeries(){
        String[] otcWithFuturesData = otcStockWithFutures.split(",");
        String[] otcWithoutFuturesData = otcStockWithoutFutures.split(",");
        return (String[]) ArrayUtils.addAll(otcWithFuturesData, otcWithoutFuturesData);
    }
}
