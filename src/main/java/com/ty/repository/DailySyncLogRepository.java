package com.ty.repository;

import org.springframework.stereotype.Repository;

import com.ty.entity.DailySyncLog;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface DailySyncLogRepository extends JpaRepository<DailySyncLog, Long>{
}
