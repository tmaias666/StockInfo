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

import org.hibernate.annotations.GenericGenerator;

import lombok.Data;

@Data
@SuppressWarnings("serial")
@Entity
@Table(name = "margin_short_trading_info")
public class MarginShortTradingInfo implements Serializable{

    @Id
    @GeneratedValue(generator = "generator")
    @GenericGenerator(name = "generator", strategy = "increment")
    private Long id;

    @Column(name = "stock_no", nullable = false) //標的代號
    private String stockNo;

    @Column(name = "info_date", nullable = false)
    private LocalDate infoDate;

    @Column(name = "margin_trading_diff", nullable = false)
    private Integer marginTradingDiff;

    @Column(name = "margin_trading_total", nullable = false)
    private Integer marginTradingTotal;

    @Column(name = "short_selling_diff", nullable = false)
    private Integer shortSellingDiff;

    @Column(name = "short_selling_total", nullable = false)
    private Integer shortSellingTotal;

    @Column(name = "security_lending_diff", nullable = false)
    private Integer securityLendingDiff;

    @Column(name = "security_lending_out_total", nullable = false)
    private Integer securityLendingOutTotal;

    @Column(name = "security_lending_total", nullable = false)
    private Integer securityLendingTotal;

    @Column(name = "short_margin_ratio", nullable = false)
    private Double shortMarginRatio;

    @Column(name = "day_trading_volumn", nullable = false)
    private Integer dayTradingVolumn;

    @Column(name = "day_trading_rate", nullable = false)
    private Double dayTradingRate;

    @Column(name = "day_trading_profit", nullable = false)
    private Double dayTradingProfit;

    @Column(name = "create_time", nullable = false)
    private Date createTime;

    @Column(name = "update_time", nullable = false)
    private Date updateTime;
}
