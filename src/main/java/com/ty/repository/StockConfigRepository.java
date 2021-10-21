package com.ty.repository;

import org.springframework.stereotype.Repository;

import com.ty.entity.StockConfig;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface StockConfigRepository extends JpaRepository<StockConfig, Long>{

    public StockConfig findByConfigName(String configName);
}
