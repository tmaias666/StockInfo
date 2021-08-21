package com.ty.repository;

import org.springframework.stereotype.Repository;

import com.ty.entity.LineUserAccount;
import com.ty.entity.StockMain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface LineUserAccountRepository extends JpaRepository<LineUserAccount, Long>{

    public LineUserAccount findByLineUid(String uid);

    public List<LineUserAccount> findByIsVerified(Integer isVerified);
}
