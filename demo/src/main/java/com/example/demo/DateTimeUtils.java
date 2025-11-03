package com.example.demo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 时间日期处理工具类
 */
public class DateTimeUtils {

    /**
     * 获取当前时间（Date类型）
     */
    public static Date now() {
        return new Date();
    }

    /**
     * 获取当前时间（LocalDateTime类型）
     */
    public static LocalDateTime nowLocal() {
        return LocalDateTime.now();
    }

    /**
     * Date转LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) return null;
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * LocalDateTime转Date
     */
    public static Date toDate(LocalDateTime ldt) {
        if (ldt == null) return null;
        return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 格式化Date为字符串
     */
    public static String format(Date date, String pattern) {
        if (date == null || pattern == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    /**
     * 格式化LocalDateTime为字符串
     */
    public static String format(LocalDateTime ldt, String pattern) {
        if (ldt == null || pattern == null) return "";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
        return ldt.format(dtf);
    }

    /**
     * 字符串转Date
     */
    public static Date parseDate(String str, String pattern) {
        if (str == null || pattern == null) return null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            return sdf.parse(str);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 字符串转LocalDateTime
     */
    public static LocalDateTime parseLocalDateTime(String str, String pattern) {
        if (str == null || pattern == null) return null;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
        try {
            return LocalDateTime.parse(str, dtf);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 日期加减（单位：天）
     */
    public static Date addDays(Date date, int days) {
        if (date == null) return null;
        LocalDateTime ldt = toLocalDateTime(date).plusDays(days);
        return toDate(ldt);
    }

    /**
     * 计算两个日期之间的天数差
     */
    public static long daysBetween(Date start, Date end) {
        if (start == null || end == null) return 0;
        LocalDate s = toLocalDateTime(start).toLocalDate();
        LocalDate e = toLocalDateTime(end).toLocalDate();
        return Duration.between(s.atStartOfDay(), e.atStartOfDay()).toDays();
    }

    /**
     * 时间戳转Date
     */
    public static Date fromTimestamp(long timestamp) {
        return new Date(timestamp);
    }

    /**
     * Date转时间戳
     */
    public static long toTimestamp(Date date) {
        if (date == null) return 0;
        return date.getTime();
    }
}