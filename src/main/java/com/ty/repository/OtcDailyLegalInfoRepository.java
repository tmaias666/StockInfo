package com.ty.repository;

import org.springframework.stereotype.Repository;

import com.ty.entity.OtcDailyLegalInfo;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface OtcDailyLegalInfoRepository extends JpaRepository<OtcDailyLegalInfo, Long>{

    @Query(value = "select info_date from stock.otc_daily_legal_info tdli where info_date <= :now order by info_date desc limit 1 ", nativeQuery = true)
    public String getLatestSyncDate(@Param("now") LocalDate now);
}
