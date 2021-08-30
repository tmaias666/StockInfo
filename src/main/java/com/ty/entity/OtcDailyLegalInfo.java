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

import lombok.Data;

@Data
@SuppressWarnings("serial")
@Entity
@Table(name = "otc_daily_legal_info")
public class OtcDailyLegalInfo extends StockLegalInfo implements Serializable{

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
}
