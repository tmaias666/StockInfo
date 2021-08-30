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

import lombok.Data;

@Data
@SuppressWarnings("serial")
@Entity
@Table(name = "master_transaction_info")
public class MasterTransactionInfo implements Serializable{

    @Id
    @GeneratedValue(generator = "generator")
    @GenericGenerator(name = "generator", strategy = "increment")
    private Long id;

    @Column(name = "stock_no", nullable = false)
    private String stockNo;

    @Column(name = "stock_name", nullable = false)
    private String stockName;

    @Column(name = "info_date", nullable = false)
    private LocalDate infoDate;

    @Column(name = "transaction_type", nullable = false)
    private Integer transactionType;

    @Column(name = "buy_volumn", nullable = false)
    private Integer buyVolumn;

    @Column(name = "sell_volumn", nullable = false)
    private Integer sellVolumn;

    @Column(name = "total_volumn", nullable = false)
    private Integer totalVolumn;

    @Column(name = "create_time", nullable = false)
    private Date createTime;
}
