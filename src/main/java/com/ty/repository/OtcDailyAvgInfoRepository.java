package com.ty.repository;

import org.springframework.stereotype.Repository;

import com.ty.entity.OtcDailyAvgInfo;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface OtcDailyAvgInfoRepository extends JpaRepository<OtcDailyAvgInfo, Long>{
}
