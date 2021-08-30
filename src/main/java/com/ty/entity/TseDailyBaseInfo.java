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
@Table(name = "tse_daily_base_info")
public class TseDailyBaseInfo implements Serializable{

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @GeneratedValue(generator = "generator")
    @GenericGenerator(name = "generator", strategy = "increment")
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
}
