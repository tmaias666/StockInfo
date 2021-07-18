package com.ty.repository;

import org.springframework.stereotype.Repository;

import com.ty.entity.AccountRecord;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface AccountRecordRepository extends JpaRepository<AccountRecord, Long>{

    @Query("select ar from AccountRecord ar where ar.name like %:name% ")
    List<AccountRecord> findByName(@Param("name")String name);
}
