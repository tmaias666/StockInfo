package com.ty.repository;

import org.springframework.stereotype.Repository;

import com.ty.entity.StockMain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface StockMainRepository extends JpaRepository<StockMain, Long>{

    public StockMain findByStockNo(String stockNo);

    @Query(value = "select s.stock_no from stock_main s where s.stock_type = :stockType ", nativeQuery = true)
    public List<String> getStockNoByStockType(@Param("stockType") int stockType);
}
