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
     * 格式化Date为字符串（线程安全）
     */
    public static String format(Date date, String pattern) {
        if (date == null || CommonUtils.isEmpty(pattern)) return "";
        return DateTimeFormatter.ofPattern(pattern)
                .format(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
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
        if (CommonUtils.isEmpty(str) || CommonUtils.isEmpty(pattern)) return null;
        try {
            LocalDateTime ldt = LocalDateTime.parse(str, DateTimeFormatter.ofPattern(pattern));
            return toDate(ldt);
        } catch (Exception e) {
            System.err.println("parseDate error: " + e.getMessage());
            return null;
        }
    }

    /**
     * 字符串转LocalDateTime
     */
    public static LocalDateTime parseLocalDateTime(String str, String pattern) {
        if (CommonUtils.isEmpty(str) || CommonUtils.isEmpty(pattern)) return null;
        try {
            return LocalDateTime.parse(str, DateTimeFormatter.ofPattern(pattern));
        } catch (Exception e) {
            System.err.println("parseLocalDateTime error: " + e.getMessage());
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
     * 计算两个日期之间的天数差（绝对值）
     */
    public static long daysBetween(Date start, Date end) {
        if (start == null || end == null) return 0;
        LocalDate s = toLocalDateTime(start).toLocalDate();
        LocalDate e = toLocalDateTime(end).toLocalDate();
        return Math.abs(Duration.between(s.atStartOfDay(), e.atStartOfDay()).toDays());
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