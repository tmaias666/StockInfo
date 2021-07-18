package com.ty.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "otc_daily_base_info")
public class OtcDailyBaseInfo implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "stock_no", nullable = false) //標的代號
    private String stockNo;

    @Column(name = "start_price", nullable = false) //當天開盤價
    private Double startPrice;

    @Column(name = "high_price", nullable = false) //當天最高價
    private Double highPrice;

    @Column(name = "low_price", nullable = false) //當天最低價
    private Double lowPrice;

    @Column(name = "end_price", nullable = false) //當天收盤價
    private Double endPrice;

    @Column(name = "k_status", nullable = false) //K棒型態
    private Integer kStatus;

    @Column(name = "diff_price", nullable = false) //當天漲跌點
    private Double diffPrice;

    @Column(name = "y_price", nullable = false) //前個交易日收盤價
    private Double yPrice;

    @Column(name = "total_volumn", nullable = false) //總交易張數
    private Integer totalVolumn;

    @Column(name = "transaction_amount", nullable = false) //總交易金額
    private BigDecimal transactionAmount;

    @Column(name = "info_date", nullable = false) //收盤資訊日期
    private LocalDate infoDate;

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

    public Double getStartPrice(){
        return startPrice;
    }

    public void setStartPrice(Double startPrice){
        this.startPrice = startPrice;
    }

    public Double getHighPrice(){
        return highPrice;
    }

    public void setHighPrice(Double highPrice){
        this.highPrice = highPrice;
    }

    public Double getLowPrice(){
        return lowPrice;
    }

    public void setLowPrice(Double lowPrice){
        this.lowPrice = lowPrice;
    }

    public Double getEndPrice(){
        return endPrice;
    }

    public void setEndPrice(Double endPrice){
        this.endPrice = endPrice;
    }

    public Integer getkStatus(){
        return kStatus;
    }

    public void setkStatus(Integer kStatus){
        this.kStatus = kStatus;
    }

    public Double getDiffPrice(){
        return diffPrice;
    }

    public void setDiffPrice(Double diffPrice){
        this.diffPrice = diffPrice;
    }

    public Double getyPrice(){
        return yPrice;
    }

    public void setyPrice(Double yPrice){
        this.yPrice = yPrice;
    }

    public Integer getTotalVolumn(){
        return totalVolumn;
    }

    public void setTotalVolumn(Integer totalVolumn){
        this.totalVolumn = totalVolumn;
    }

    public BigDecimal getTransactionAmount(){
        return transactionAmount;
    }

    public void setTransactionAmount(BigDecimal transactionAmount){
        this.transactionAmount = transactionAmount;
    }

    public LocalDate getInfoDate(){
        return infoDate;
    }

    public void setInfoDate(LocalDate infoDate){
        this.infoDate = infoDate;
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
