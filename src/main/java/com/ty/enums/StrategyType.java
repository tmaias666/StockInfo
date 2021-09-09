package com.ty.enums;

public enum StrategyType{

    BullStrategy1(11),
    BullStrategy2(12),
    BearStrategy1(21),
    BearStrategy2(22);

    private Integer value;

    private StrategyType(Integer value){
        this.value = value;
    }

    public Integer getValue(){
        return value;
    }
}
