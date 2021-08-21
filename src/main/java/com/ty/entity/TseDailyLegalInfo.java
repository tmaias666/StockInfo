package com.ty.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.ty.vo.StockLegalInfo;

@SuppressWarnings("serial")
@Entity
@Table(name = "tse_daily_legal_info")
public class TseDailyLegalInfo extends StockLegalInfo implements Serializable{

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @GeneratedValue(generator = "generator")
    @GenericGenerator(name = "generator", strategy = "increment")
    private Long id;

    @Column(name = "stock_no", nullable = false)
    private String stockNo;

    @Column(name = "info_date", nullable = false)
    private LocalDate infoDate;

    @Column(name = "foreign_investor", nullable = false)
    private Integer foreignInvestor;

    @Column(name = "investment_trust", nullable = false)
    private Integer investmentTrust;

    @Column(name = "dealer_self", nullable = false)
    private Integer dealerSelf;

    @Column(name = "dealer_hedging", nullable = false)
    private Integer dealerHedging;

    @Column(name = "create_time", nullable = false)
    private Date createTime;

    @Column(name = "update_time", nullable = false)
    private Date updateTime;

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

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
