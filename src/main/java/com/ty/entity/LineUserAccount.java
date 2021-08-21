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
@Table(name = "line_user_account")
public class LineUserAccount implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "line_uid", nullable = false)
    private String lineUid;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "create_time", nullable = false)
    private Date createTime;

    @Column(name = "is_verified", nullable = false)
    private Integer isVerified;

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getLineUid(){
        return lineUid;
    }

    public void setLineUid(String lineUid){
        this.lineUid = lineUid;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public Date getCreateTime(){
        return createTime;
    }

    public void setCreateTime(Date createTime){
        this.createTime = createTime;
    }

    public Integer getIsVerified(){
        return isVerified;
    }

    public void setIsVerified(Integer isVerified){
        this.isVerified = isVerified;
    }
}
