package com.ty.mybatis.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.ty.entity.LineUserAccount;

@Mapper //表示這是Mybatis的mapper類
@Repository
public interface LineUserAccountMapper{

    LineUserAccount findByLineUid(String uid);
}
