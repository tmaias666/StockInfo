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

import lombok.Data;

@Data
@SuppressWarnings("serial")
@Entity
@Table(name = "daily_sync_log")
public class DailySyncLog implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_type", nullable = false)
    private String jobType;

    @Column(name = "sync_result", nullable = false)
    private String syncResult;

    @Column(name = "sync_message", nullable = false)
    private String syncMessage;

    @Column(name = "info_date", nullable = false)
    private LocalDate infoDate;

    @Column(name = "create_time", nullable = false)
    private Date createTime;
}
