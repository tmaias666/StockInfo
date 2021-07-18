package com.ty.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@SuppressWarnings("serial")
@Entity
@Table(name = "otc_daily_avg_info")
public class OtcDailyAvgInfo implements Serializable{

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @GeneratedValue(generator = "generator")
    @GenericGenerator(name = "generator", strategy = "increment")
    private Long id;

    @Column(name = "stock_no", nullable = false)
    private String stockNo;

    @Column(name = "info_date", nullable = false)
    private LocalDate infoDate;

    @Column(name = "five_avg_price", nullable = false)
    private Double fiveAvgPrice;

    @Column(name = "five_avg_direction", nullable = false)
    private Integer fiveAvgDirection;

    @Column(name = "ten_avg_price", nullable = false)
    private Double tenAvgPrice;

    @Column(name = "ten_avg_direction", nullable = false)
    private Integer tenAvgDirection;

    @Column(name = "month_avg_price", nullable = false)
    private Double monthAvgPrice;

    @Column(name = "month_avg_direction", nullable = false)
    private Integer monthAvgDirection;

    @Column(name = "season_avg_price", nullable = false)
    private Double seasonAvgPrice;

    @Column(name = "season_avg_direction", nullable = false)
    private Integer seasonAvgDirection;

    @Column(name = "half_year_avg_price", nullable = false)
    private Double halfYearAvgPrice;

    @Column(name = "half_year_avg_direction", nullable = false)
    private Integer halfYearAvgDirection;

    @Column(name = "year_avg_price")
    private Double yearAvgPrice;

    @Column(name = "year_avg_direction", nullable = false)
    private Integer yearAvgDirection;

    @Column(name = "bollinger_top", nullable = false)
    private Double bollingerTop;

    @Column(name = "bollinger_bottom")
    private Double bollingerBottom;

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
