package com.ty.enums;

public enum StockType{

    TSE(0),
    OTC(1),
    ALL(2);

    private Integer value;

    private StockType(Integer value){
        this.value = value;
    }

    public Integer getValue(){
        return value;
    }
}
