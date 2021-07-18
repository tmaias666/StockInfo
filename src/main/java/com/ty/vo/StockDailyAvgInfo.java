package com.ty.vo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

public class StockDailyAvgInfo implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String stockNo;

    private LocalDate infoDate;

    private Double fiveAvgPrice;

    private Integer fiveAvgDirection;

    private Double tenAvgPrice;

    private Integer tenAvgDirection;

    private Double monthAvgPrice;

    private Integer monthAvgDirection;

    private Double seasonAvgPrice;

    private Integer seasonAvgDirection;

    private Double halfYearAvgPrice;

    private Integer halfYearAvgDirection;

    private Double yearAvgPrice;

    private Integer yearAvgDirection;

    private Double bollingerTop;

    private Double bollingerBottom;

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

    public Double getFiveAvgPrice(){
        return fiveAvgPrice;
    }

    public void setFiveAvgPrice(Double fiveAvgPrice){
        this.fiveAvgPrice = fiveAvgPrice;
    }

    public Integer getFiveAvgDirection(){
        return fiveAvgDirection;
    }

    public void setFiveAvgDirection(Integer fiveAvgDirection){
        this.fiveAvgDirection = fiveAvgDirection;
    }

    public Double getTenAvgPrice(){
        return tenAvgPrice;
    }

    public void setTenAvgPrice(Double tenAvgPrice){
        this.tenAvgPrice = tenAvgPrice;
    }

    public Integer getTenAvgDirection(){
        return tenAvgDirection;
    }

    public void setTenAvgDirection(Integer tenAvgDirection){
        this.tenAvgDirection = tenAvgDirection;
    }

    public Double getMonthAvgPrice(){
        return monthAvgPrice;
    }

    public void setMonthAvgPrice(Double monthAvgPrice){
        this.monthAvgPrice = monthAvgPrice;
    }

    public Integer getMonthAvgDirection(){
        return monthAvgDirection;
    }

    public void setMonthAvgDirection(Integer monthAvgDirection){
        this.monthAvgDirection = monthAvgDirection;
    }

    public Double getSeasonAvgPrice(){
        return seasonAvgPrice;
    }

    public void setSeasonAvgPrice(Double seasonAvgPrice){
        this.seasonAvgPrice = seasonAvgPrice;
    }

    public Integer getSeasonAvgDirection(){
        return seasonAvgDirection;
    }

    public void setSeasonAvgDirection(Integer seasonAvgDirection){
        this.seasonAvgDirection = seasonAvgDirection;
    }

    public Double getHalfYearAvgPrice(){
        return halfYearAvgPrice;
    }

    public void setHalfYearAvgPrice(Double halfYearAvgPrice){
        this.halfYearAvgPrice = halfYearAvgPrice;
    }

    public Integer getHalfYearAvgDirection(){
        return halfYearAvgDirection;
    }

    public void setHalfYearAvgDirection(Integer halfYearAvgDirection){
        this.halfYearAvgDirection = halfYearAvgDirection;
    }

    public Double getYearAvgPrice(){
        return yearAvgPrice;
    }

    public void setYearAvgPrice(Double yearAvgPrice){
        this.yearAvgPrice = yearAvgPrice;
    }

    public Integer getYearAvgDirection(){
        return yearAvgDirection;
    }

    public void setYearAvgDirection(Integer yearAvgDirection){
        this.yearAvgDirection = yearAvgDirection;
    }

    public Double getBollingerTop(){
        return bollingerTop;
    }

    public void setBollingerTop(Double bollingerTop){
        this.bollingerTop = bollingerTop;
    }

    public Double getBollingerBottom(){
        return bollingerBottom;
    }

    public void setBollingerBottom(Double bollingerBottom){
        this.bollingerBottom = bollingerBottom;
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
