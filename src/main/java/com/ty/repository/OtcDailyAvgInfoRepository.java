package com.ty.repository;

import org.springframework.stereotype.Repository;

import com.ty.entity.OtcDailyAvgInfo;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface OtcDailyAvgInfoRepository extends JpaRepository<OtcDailyAvgInfo, Long>{

    //[多方策略1]上櫃均線多排且收盤站上5日均 + 法人買超
    @Query(value = "select '上櫃' as '上市櫃', m.stock_name as '股名', odli.stock_no as '股號',  "
        +"odli.foreign_investor as '外資買賣超', odli.investment_trust as '投信買賣超', "
        +"odli.dealer_self as '自營商買賣超', odli.dealer_hedging as '自營商避險', "
        +"t.end_price as '收盤價', t.diff_price as '今日漲跌點' "
        +"from (select odai.stock_no, odbi.end_price, odbi.diff_price from stock.otc_daily_base_info odbi "
        +"inner join stock.otc_daily_avg_info odai "
        +"on odbi.stock_no = odai.stock_no and odbi.info_date = odai.info_date "
        +"where odbi.info_date = :infoDate "
        +"and odbi.end_price > odai.five_avg_price "
        +"and odai.five_avg_price > odai.ten_avg_price "
        +"and odai.ten_avg_price > odai.month_avg_price "
        +"and odai.month_avg_price > odai.season_avg_price) t "
        +"inner join stock.otc_daily_legal_info odli "
        +"inner join stock.stock_main m "
        +"on odli.stock_no = t.stock_no and t.stock_no = m.stock_no "
        +"where odli.info_date = :infoDate "
        +"and (odli.investment_trust > 0 and odli.foreign_investor > 0) "
        +"order by (odli.investment_trust + odli.foreign_investor) desc", nativeQuery = true)
    public List<Map<String, Object>> getOtcBullStrategy1(@Param("infoDate") LocalDate infoDate);

    //[空方策略1]上櫃收盤在月季線下+法人賣超排行
    @Query(value = "select '上櫃' as '上市櫃', m.stock_name as '股名', odli.stock_no as '股號',  "
        +"odli.foreign_investor as '外資買賣超',  odli.investment_trust as '投信買賣超', "
        +"odli.dealer_self as '自營商買賣超', odli.dealer_hedging as '自營商避險', "
        +"t.end_price as '收盤價', t.diff_price as '今日漲跌點'  "
        +"from (select odai.stock_no, odbi.end_price, odbi.diff_price from stock.otc_daily_base_info odbi "
        +"inner join stock.otc_daily_avg_info odai "
        +"on odbi.stock_no = odai.stock_no and odbi.info_date = odai.info_date "
        +"where odbi.info_date = :infoDate "
        +"and (odbi.end_price < odai.month_avg_price and odbi.end_price < odai.season_avg_price)) t "
        +"inner join stock.otc_daily_legal_info odli "
        +"inner join stock.stock_main m "
        +"on odli.stock_no = t.stock_no and t.stock_no = m.stock_no "
        +"where odli.info_date = :infoDate "
        +"and (odli.investment_trust < 0 and odli.foreign_investor < 0) "
        +"order by (odli.investment_trust + odli.foreign_investor) asc", nativeQuery = true)
    public List<Map<String, Object>> getOtcBearStrategy1(@Param("infoDate") LocalDate infoDate);
}
