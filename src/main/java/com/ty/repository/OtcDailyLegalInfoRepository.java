package com.ty.repository;

import org.springframework.stereotype.Repository;

import com.ty.entity.OtcDailyLegalInfo;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface OtcDailyLegalInfoRepository extends JpaRepository<OtcDailyLegalInfo, Long>{
}
