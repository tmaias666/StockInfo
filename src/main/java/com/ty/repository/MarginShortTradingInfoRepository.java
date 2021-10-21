package com.ty.repository;

import org.springframework.stereotype.Repository;

import com.ty.entity.MarginShortTradingInfo;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface MarginShortTradingInfoRepository extends JpaRepository<MarginShortTradingInfo, Long>{
}
