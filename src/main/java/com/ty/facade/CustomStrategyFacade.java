package com.ty.facade;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.ty.facade.impl.JasonStrategyFacadeImpl;
import com.ty.interfaces.CustomStrategy;

@Service
public class CustomStrategyFacade{

    @Autowired
    private JasonStrategyFacadeImpl jasonStrategyFacadeImpl;

    //private CustomStrategy jasonStrategy;

    public CustomStrategyFacade(){
    }

    public String getJasonStartegyMessage(LocalDate queryDate){
        return jasonStrategyFacadeImpl.getCustomStrategyMessage(queryDate);
    }
}
