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
        +"from stock.otc_daily_base_info odbi, stock.otc_daily_legal_info odli, "
        +"stock.stock_main m where odli.stock_no = m.stock_no "
        +"and odli.stock_no = odbi.stock_no and odli.info_date = odbi.info_date "
        +"and odli.info_date > :startDate and odli.info_date < :endDate) t "
        +"group by t.stock_name order by (sum(t.foreign_investor) + sum(t.investment_trust)) desc", nativeQuery = true)
    public List<Map<String, Object>> getFiAndItBuySellStrategy(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    
    //取個股近幾日的總交易量
    @Query(value = "select t.stock_no, sum(t.total_volumn) as 'sumVolumn' from "
        +"(select odbi.stock_no, odbi.info_date, odbi.total_volumn from stock.otc_daily_base_info odbi "
        +"where odbi.stock_no in :stockNoList order by odbi.info_date desc limit :limit) t "
        +"group by t.stock_no ", nativeQuery = true)
    public List<Map<String, Object>> getRecentTotalVolumn(@Param("stockNoList") List<String> stockNoList, @Param("limit") Integer limit);
    
    //客製策略一
    @Query(value = "select odbi.stock_no, sm.stock_name, odbi.diff_price, "
        +"odbi.start_price, odbi.end_price, odbi.high_price, odbi.low_price, odai.five_avg_price "
        +"from stock.stock_main sm "
        +"inner join stock.otc_daily_base_info odbi on sm.stock_no = odbi.stock_no "
        +"inner join stock.otc_daily_avg_info odai on sm.stock_no = odai.stock_no "
        +"where odbi.info_date = :infoDate and odai.info_date = :infoDate "
        +"and odbi.k_status = 1 and odbi.low_price >= odai.five_avg_price ", nativeQuery = true)
    public List<Map<String, Object>> getCustomStrategy1(@Param("infoDate") LocalDate infoDate);
}
