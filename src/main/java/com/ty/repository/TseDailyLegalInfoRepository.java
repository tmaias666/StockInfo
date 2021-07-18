package com.ty.repository;

import org.springframework.stereotype.Repository;

import com.ty.entity.TseDailyLegalInfo;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface TseDailyLegalInfoRepository extends JpaRepository<TseDailyLegalInfo, Long>{
}
