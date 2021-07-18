package com.ty.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ty.entity.AccountRecord;
import com.ty.repository.AccountRecordRepository;

import java.util.List;

@Service
public class AccountService{

    @Autowired
    private AccountRecordRepository accountRecordRepository;

    public List<AccountRecord> getAccountList(String name){
        return accountRecordRepository.findByName(name);
    }
}
