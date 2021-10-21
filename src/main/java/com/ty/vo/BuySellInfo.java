package com.ty.vo;

import lombok.Data;

@Data
public class BuySellInfo{

    private String stockNo;
    
    private String stockName;
    
    private int buyVolumn;

    private int sellVolumn;

    private int totalVolumn;
}
