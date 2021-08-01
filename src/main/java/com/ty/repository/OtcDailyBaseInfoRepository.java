package com.ty.repository;

import org.springframework.stereotype.Repository;

import com.ty.entity.OtcDailyBaseInfo;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface OtcDailyBaseInfoRepository extends JpaRepository<OtcDailyBaseInfo, Long>{

    //回溯重建一年資料
    @Query(value = "select odbi.info_date as 'infoDate', odbi.end_price as 'endPrice' from stock.otc_daily_base_info odbi where odbi.stock_no = :stockNo and odbi.info_date > '2020-07-14' order by odbi.info_date asc ", nativeQuery = true)
    public List<Map<String, Object>> getDailyEndPriceInfoForRebuild(@Param("stockNo") String stockNo);

    //同步當日資料
    @Query(value = "select odbi.info_date as 'infoDate', odbi.end_price as 'endPrice' from stock.otc_daily_base_info odbi where odbi.stock_no = :stockNo order by info_date desc limit 241 ", nativeQuery = true)
    public List<Map<String, Object>> getEndPriceInfoForDailySync(@Param("stockNo") String stockNo);

    //抓每檔個股前三高價資料
    @Query(value = "select stock_no, end_price, info_date  from "
        +"(select odbi.stock_no, odbi.end_price, odbi.info_date RANK() over "
        +"(PARTITION BY stock_no ORDER by end_price desc ) AS class_rank from stock.otc_daily_base_info odbi) d "
        +"where d.class_rank < 4 order by stock_no, end_price desc ", nativeQuery = true)
    public List<Map<String, Object>> getTop3EndPriceByGroupStockNo();

    //抓每週外資投信買超總和排行
    @Query(value = "select '上櫃' as '上市櫃', t.stock_name, sum(t.foreign_investor) as 'fi', "
        +"sum(t.investment_trust) as 'it', sum(t.dealer_self) as 'ds', "
        +"sum(t.dealer_hedging) as 'dh' from "
        +"(select m.stock_name, odli.info_date, odli.foreign_investor, odli.investment_trust, odli.dealer_self, odli.dealer_hedging "
        +"from stock.otc_daily_legal_info odli "
        +"inner join stock.stock_main m on odli.stock_no = m.stock_no "
        +"where odli.info_date > :startDate and odli.info_date < :endDate) t "
        +"group by t.stock_name order by (sum(t.foreign_investor) + sum(t.investment_trust)) desc ", nativeQuery = true)
    public List<Map<String, Object>> getWeeklyFiAndItStrategy(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
