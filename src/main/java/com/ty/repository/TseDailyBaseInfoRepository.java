package com.ty.repository;

import org.springframework.stereotype.Repository;

import com.ty.entity.TseDailyBaseInfo;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface TseDailyBaseInfoRepository extends JpaRepository<TseDailyBaseInfo, Long>{

    //回溯重建一年資料
    @Query(value = "select tdbi.info_date as 'infoDate', tdbi.end_price as 'endPrice' from stock.tse_daily_base_info tdbi where tdbi.stock_no = :stockNo and tdbi.info_date > '2020-07-14' order by tdbi.info_date asc ", nativeQuery = true)
    public List<Map<String, Object>> getDailyEndPriceInfoForRebuild(@Param("stockNo") String stockNo);

    //同步當日資料
    @Query(value = "select tdbi.info_date as 'infoDate', tdbi.end_price as 'endPrice' from stock.tse_daily_base_info tdbi where tdbi.stock_no = :stockNo order by info_date desc limit 241 ", nativeQuery = true)
    public List<Map<String, Object>> getEndPriceInfoForDailySync(@Param("stockNo") String stockNo);
}
