package com.ty.repository;

import org.springframework.stereotype.Repository;

import com.ty.entity.MasterTransactionInfo;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface MasterTransactionInfoRepository extends JpaRepository<MasterTransactionInfo, Long>{
}
