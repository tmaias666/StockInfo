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

import lombok.Data;

@Data
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
}
