package com.example.demo;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * 通用工具类，封装常用静态方法
 */
public class CommonUtils {

    /**
     * 判断字符串是否为空
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 判断集合是否为空
     */
    public static boolean isEmpty(Collection<?> coll) {
        return coll == null || coll.isEmpty();
    }

    /**
     * 判断Map是否为空
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * 格式化日期为字符串
     */
    public static String formatDate(Date date, String pattern) {
        if (date == null || isEmpty(pattern)) return "";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    /**
     * 安全地将对象转为字符串
     */
    public static String toString(Object obj) {
        return obj == null ? "" : obj.toString();
    }

    /**
     * 判断字符串是否为数字
     */
    public static boolean isNumeric(String str) {
        if (isEmpty(str)) return false;
        return str.matches("\\d+");
    }

    /**
     * 首字母大写
     */
    public static String capitalize(String str) {
        if (isEmpty(str)) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}