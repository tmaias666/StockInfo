package com.ty.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.ty.entity.OtcDailyAvgInfo;
import com.ty.entity.TseDailyAvgInfo;
import com.ty.vo.StockDailyAvgInfo;

@Mapper
public interface StockAvgInfoMapper{

    StockAvgInfoMapper INSTANCE = Mappers.getMapper(StockAvgInfoMapper.class);

    TseDailyAvgInfo toDTO(StockDailyAvgInfo stockAvgPriceInfo);

    OtcDailyAvgInfo toVO(StockDailyAvgInfo stockAvgPriceInfo);
}
