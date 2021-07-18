package com.ty.repository;

import org.springframework.stereotype.Repository;

import com.ty.entity.TseDailyAvgInfo;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface TseDailyAvgInfoRepository extends JpaRepository<TseDailyAvgInfo, Long>{
}
