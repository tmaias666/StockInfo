package com.ty.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "stock_main")
public class StockMain implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "stock_no", nullable = false)
    private String stockNo;

    @Column(name = "stock_name", nullable = false)
    private String stockName;

    @Column(name = "stock_type", nullable = false)
    private Integer stockType;

    @Column(name = "stock_future", nullable = false)
    private Integer stockFuture;

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

    public String getStockName(){
        return stockName;
    }

    public void setStockName(String stockName){
        this.stockName = stockName;
    }

    public Integer getStockType(){
        return stockType;
    }

    public void setStockType(Integer stockType){
        this.stockType = stockType;
    }

    public Integer getStockFuture(){
        return stockFuture;
    }

    public void setStockFuture(Integer stockFuture){
        this.stockFuture = stockFuture;
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
