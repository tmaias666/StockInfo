package com.ty.vo;

import java.time.LocalDate;
import java.util.Date;

import javax.persistence.Column;

public class StockLegalInfo{

    private String stockNo;

    private LocalDate infoDate;

    private Integer foreignInvestor;

    private Integer investmentTrust;

    private Integer dealerSelf;

    private Integer dealerHedging;

    private Date createTime;

    private Date updateTime;

    public String getStockNo(){
        return stockNo;
    }

    public void setStockNo(String stockNo){
        this.stockNo = stockNo;
    }

    public LocalDate getInfoDate(){
        return infoDate;
    }

    public void setInfoDate(LocalDate infoDate){
        this.infoDate = infoDate;
    }

    public Integer getForeignInvestor(){
        return foreignInvestor;
    }

    public void setForeignInvestor(Integer foreignInvestor){
        this.foreignInvestor = foreignInvestor;
    }

    public Integer getInvestmentTrust(){
        return investmentTrust;
    }

    public void setInvestmentTrust(Integer investmentTrust){
        this.investmentTrust = investmentTrust;
    }

    public Integer getDealerSelf(){
        return dealerSelf;
    }

    public void setDealerSelf(Integer dealerSelf){
        this.dealerSelf = dealerSelf;
    }

    public Integer getDealerHedging(){
        return dealerHedging;
    }

    public void setDealerHedging(Integer dealerHedging){
        this.dealerHedging = dealerHedging;
    }

    public Date getCreateTime(){
        return createTime;
    }

    public void setCreateTime(Date createTime){
        this.createTime = createTime;
    }

    public Date getUpdateTime(){
        return updateTime;
    }

    public void setUpdateTime(Date updateTime){
        this.updateTime = updateTime;
    }
}
