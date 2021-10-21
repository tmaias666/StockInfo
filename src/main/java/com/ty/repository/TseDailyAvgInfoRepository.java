package com.ty.repository;

import org.springframework.stereotype.Repository;

import com.ty.entity.TseDailyAvgInfo;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface TseDailyAvgInfoRepository extends JpaRepository<TseDailyAvgInfo, Long>{

    @Query(value = "select info_date from stock.tse_daily_avg_info tdai where info_date <= :now order by info_date desc limit 1 ", nativeQuery = true)
    public String getLatestSyncDate(@Param("now") LocalDate now);
    
    //[多方策略1]上市均線多排且收盤站上5日均 + 法人買超
    @Query(value = "select '上市' as '上市櫃', m.stock_name as '股名', tdli.stock_no as '股號', "
        +"tdli.foreign_investor as '外資買賣超', tdli.investment_trust as '投信買賣超', "
        +"tdli.dealer_self as '自營商買賣超', tdli.dealer_hedging as '自營商避險', "
        +"t.end_price as '收盤價', t.diff_price as '漲跌點' "
        +"from (select tdai.stock_no, tdbi.end_price, tdbi.diff_price from stock.tse_daily_base_info tdbi "
        +"inner join stock.tse_daily_avg_info tdai "
        +"on tdbi.stock_no = tdai.stock_no and tdbi.info_date = tdai.info_date "
        +"where tdbi.info_date = :infoDate "
        +"and tdbi.end_price > tdai.five_avg_price "
        +"and tdai.five_avg_price > tdai.ten_avg_price "
        +"and tdai.ten_avg_price > tdai.month_avg_price "
        +"and tdai.month_avg_price > tdai.season_avg_price) t "
        +"inner join stock.tse_daily_legal_info tdli "
        +"inner join stock.stock_main m "
        +"on tdli.stock_no = t.stock_no and t.stock_no = m.stock_no "
        +"where tdli.info_date = :infoDate "
        +"and (tdli.investment_trust > -1 and tdli.foreign_investor > -1) "
        +"order by (tdli.investment_trust + tdli.foreign_investor) desc ", nativeQuery = true)
    public List<Map<String, Object>> getTseBullStrategy1(@Param("infoDate") LocalDate infoDate);

    //[多方策略2]上市收盤在月季線上+外投買
    @Query(value = "select '上市' as '上市櫃', m.stock_name as '股名', tdli.stock_no as '股號', "
        +"tdli.foreign_investor as '外資買賣超', tdli.investment_trust as '投信買賣超', "
        +"tdli.dealer_self as '自營商買賣超', tdli.dealer_hedging as '自營商避險', "
        +"t.end_price as '收盤價', t.diff_price as '漲跌點'  "
        +"from (select tdai.stock_no, tdbi.end_price, tdbi.diff_price from stock.tse_daily_base_info tdbi "
        +"inner join stock.tse_daily_avg_info tdai "
        +"on tdbi.stock_no = tdai.stock_no and tdbi.info_date = tdai.info_date "
        +"where tdbi.info_date = :infoDate "
        +"and (tdbi.end_price > tdai.month_avg_price "
        +"and tdbi.end_price > tdai.season_avg_price)) t "
        +"inner join stock.tse_daily_legal_info tdli "
        +"inner join stock.stock_main m "
        +"on tdli.stock_no = t.stock_no and t.stock_no = m.stock_no "
        +"where tdli.info_date = :infoDate "
        +"and (tdli.investment_trust > 0 and tdli.foreign_investor > 0)"
        +"order by (tdli.investment_trust + tdli.foreign_investor) desc ", nativeQuery = true)
    public List<Map<String, Object>> getTseBullStrategy2(@Param("infoDate") LocalDate infoDate);
    
    
    
    //[空方策略1]5,10,20ma下彎且外投賣
    @Query(value = "select '上市' as '上市櫃', m.stock_name as '股名', tdli.stock_no as '股號', "
        +"tdli.foreign_investor as '外資買賣超', "
        +"tdli.investment_trust as '投信買賣超', "
        +"tdli.dealer_self as '自營商買賣超', "
        +"tdli.dealer_hedging as '自營商避險', "
        +"t.end_price as '收盤價', t.diff_price as '漲跌點' "
        +"from (select tdai.stock_no, tdbi.end_price, tdbi.diff_price from stock.tse_daily_base_info tdbi "
        +"inner join stock.tse_daily_avg_info tdai "
        +"on tdbi.stock_no = tdai.stock_no and tdbi.info_date = tdai.info_date "
        +"where tdbi.info_date = :infoDate "
        +"and tdai.five_avg_direction = 2 and tdai.ten_avg_direction = 2 "
        +"and tdai.month_avg_direction = 2) t "
        +"inner join stock.tse_daily_legal_info tdli "
        +"inner join stock.stock_main m "
        +"on tdli.stock_no = t.stock_no and t.stock_no = m.stock_no "
        +"where tdli.info_date = :infoDate "
        +"and (tdli.investment_trust < 0  and tdli.foreign_investor < 0) "
        +"order by (tdli.investment_trust + tdli.foreign_investor) ", nativeQuery = true)
    public List<Map<String, Object>> getTseBearStrategy1(@Param("infoDate") LocalDate infoDate);
    
    //[空方策略2]上市收盤在月季線下+外投賣
    @Query(value = "select '上市' as '上市櫃', m.stock_name as '股名', tdli.stock_no as '股號', "
        +"tdli.foreign_investor as '外資買賣超', tdli.investment_trust as '投信買賣超', "
        +"tdli.dealer_self as '自營商買賣超', tdli.dealer_hedging as '自營商避險', "
        +"t.end_price as '收盤價', t.diff_price as '漲跌點'  "
        +"from (select tdai.stock_no, tdbi.end_price, tdbi.diff_price from stock.tse_daily_base_info tdbi "
        +"inner join stock.tse_daily_avg_info tdai "
        +"on tdbi.stock_no = tdai.stock_no and tdbi.info_date = tdai.info_date "
        +"where tdbi.info_date = :infoDate "
        +"and (tdbi.end_price < tdai.month_avg_price "
        +"and tdbi.end_price < tdai.season_avg_price)) t "
        +"inner join stock.tse_daily_legal_info tdli "
        +"inner join stock.stock_main m "
        +"on tdli.stock_no = t.stock_no and t.stock_no = m.stock_no "
        +"where tdli.info_date = :infoDate "
        +"and (tdli.investment_trust < 0 and tdli.foreign_investor < 0)"
        +"order by (tdli.investment_trust + tdli.foreign_investor) asc ", nativeQuery = true)
    public List<Map<String, Object>> getTseBearStrategy2(@Param("infoDate") LocalDate infoDate);
}
