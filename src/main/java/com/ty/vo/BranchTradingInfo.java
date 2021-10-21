package com.ty.vo;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;

import lombok.Data;

@Data
public class BranchTradingInfo{

    private LocalDate infoDate;

    private String branchId;

    private String branchName;

    private List<BuySellInfo> buyInfoList;

    private List<BuySellInfo> sellInfoList;
}
