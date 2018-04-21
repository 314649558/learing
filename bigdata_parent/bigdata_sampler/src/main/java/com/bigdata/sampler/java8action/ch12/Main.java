package com.bigdata.sampler.java8action.ch12;

import java.time.*;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.Chronology;
import java.time.chrono.HijrahDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.*;
import java.util.Locale;

/**
 * Created by Administrator on 2017/12/5 0005.
 */
public class Main {

    public static void testLocalDate(){
        LocalDate localDate=LocalDate.of(2016,3,19);

        System.out.println("year:"+ localDate.get(ChronoField.YEAR));
        System.out.println("month:"+localDate.getMonth());
        System.out.println("DayOfYear:"+localDate.getDayOfYear());
        System.out.println("DayOfMonth:"+localDate.getDayOfMonth());
        System.out.println("getDayOfWeek:"+localDate.getDayOfWeek());
        System.out.println("lengthOfMonth:"+localDate.lengthOfMonth());
        System.out.println("isLeapYear:"+localDate.isLeapYear());

        System.out.println("today:"+LocalDate.now());

        System.out.println(localDate.withDayOfMonth(10));
        System.out.println(localDate.plusDays(10));
    }

    public static void testLocalDatetime(){
        LocalDateTime localDateTime=LocalDateTime.of(2016,3,19,23,23,23);
        System.out.println("year:"+ localDateTime.get(ChronoField.YEAR));
        System.out.println("month:"+localDateTime.getMonth());
        System.out.println("DayOfYear:"+localDateTime.getDayOfYear());
        System.out.println("DayOfMonth:"+localDateTime.getDayOfMonth());
        System.out.println("getDayOfWeek:"+localDateTime.getDayOfWeek());
        System.out.println("getDayOfWeek:"+localDateTime.getHour());
    }


    public static void testInstant(){
        Instant instant=Instant.now();
        System.out.println(instant);

        System.out.println(instant.ofEpochSecond(2));

    }

    public static void testDuration(){
        LocalDateTime localDateTime1=LocalDateTime.of(2016,1,18,17,23,23);
        LocalDateTime localDateTime2=LocalDateTime.of(2016,1,19,23,23,23);

        Duration duration=Duration.between(localDateTime1,localDateTime2);
        System.out.println(duration.toHours());

    }


    public static void testTemporalAdjuster(){
        LocalDate localDate=LocalDate.now();

        System.out.println(localDate.with(TemporalAdjusters.firstDayOfMonth()));//一个月中的第一天
        System.out.println(localDate.with(TemporalAdjusters.lastDayOfMonth()));//一个月中最后一天
        System.out.println(localDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY)));//下一个星期一
        System.out.println(localDate.with(TemporalAdjusters.next(DayOfWeek.MONDAY)));//下一个星期一
        System.out.println(localDate.with(TemporalAdjusters.lastInMonth(DayOfWeek.MONDAY)));//这个月中最后一个星期一
        System.out.println(localDate.with(TemporalAdjusters.lastDayOfYear()));//一年中的最后一天


        System.out.println(localDate.with(new NextWorkingDay()));  //下一个工作日

        System.out.println("-------格式化-----");
        System.out.println(localDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
        System.out.println(localDate.format(DateTimeFormatter.BASIC_ISO_DATE));

        DateTimeFormatter formatter=DateTimeFormatter.ofPattern("dd/MM/yyyy");

        System.out.println(localDate.format(formatter));


       // OffsetDateTime.of(LocalDateTime.now(),);


        Chronology china=Chronology.ofLocale(Locale.CHINA);

        ChronoLocalDate date=china.dateNow();

        System.out.println(date.get(ChronoField.YEAR));



        //伊斯兰教日期
        HijrahDate hijrahDate=
                HijrahDate.now().with(ChronoField.DAY_OF_MONTH,1)
                .with(ChronoField.MONTH_OF_YEAR,9);


    }


    public static void main(String[] args) {
       //testLocalDate();
        //testLocalDatetime();
        //testInstant();

        //testDuration();

        testTemporalAdjuster();
    }
}
