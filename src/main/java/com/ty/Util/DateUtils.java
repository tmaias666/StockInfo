package com.ty.Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.Chronology;
import java.time.chrono.MinguoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DecimalStyle;
import java.util.Locale;

@Service
public class DateUtils{

    private static final Logger logger = LoggerFactory.getLogger(DateUtils.class);

    public static final String todayDate = LocalDate.now().toString();

    public static LocalDate transferMinguoDateToADDate(String dateString){
        Chronology chrono = MinguoChronology.INSTANCE;
        DateTimeFormatter df = new DateTimeFormatterBuilder().parseLenient().appendPattern("yyy/MM/dd").toFormatter().withChronology(chrono).withDecimalStyle(DecimalStyle.of(Locale.getDefault()));
        ChronoLocalDate chDate = chrono.date(df.parse(dateString));
        return LocalDate.from(chDate);
        //return LocalDate.from(chDate).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }
}
