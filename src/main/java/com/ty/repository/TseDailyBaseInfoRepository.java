package com.ty.repository;

import org.springframework.stereotype.Repository;

import com.ty.entity.TseDailyBaseInfo;

import java.time.LocalDate;
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

    //抓每檔個股前三高價資料
    @Query(value = "select stock_no, end_price, info_date  from "
        +"(select tdbi.stock_no, tdbi.end_price, tdbi.info_date, RANK() over "
        +"(PARTITION BY stock_no ORDER by end_price desc ) AS class_rank from stock.tse_daily_base_info tdbi) d "
        +"where d.class_rank < 4 order by stock_no, end_price desc ", nativeQuery = true)
    public List<Map<String, Object>> getTop3EndPriceByGroupStockNo();
    
    //抓每週外資投信買超總和排行
    @Query(value = "select '上市' as '上市櫃', t.stock_name, sum(t.foreign_investor) as 'fi', "
        +"sum(t.investment_trust) as 'it', sum(t.dealer_self) as 'ds', "
        +"sum(t.dealer_hedging) as 'dh' from "
        +"(select m.stock_name, tdli.info_date, tdli.foreign_investor, tdli.investment_trust, tdli.dealer_self, tdli.dealer_hedging "
        +"from stock.tse_daily_legal_info tdli "
        +"inner join stock.stock_main m on tdli.stock_no = m.stock_no "
        +"where tdli.info_date > :startDate and tdli.info_date < :endDate) t "
        +"group by t.stock_name order by (sum(t.foreign_investor) + sum(t.investment_trust)) desc ", nativeQuery = true)
    public List<Map<String, Object>> getWeeklyFiAndItStrategy(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
