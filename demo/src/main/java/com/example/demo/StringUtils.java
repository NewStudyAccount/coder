package com.example.demo;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 字符串处理工具类，封装常用字符串操作，全部为静态方法。
 */
public class StringUtils {

    /** 判空 */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /** 判非空 */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /** 去除首尾空格（null安全） */
    public static String trim(String str) {
        return str == null ? null : str.trim();
    }

    /** 字符串反转 */
    public static String reverse(String str) {
        if (str == null) return null;
        return new StringBuilder(str).reverse().toString();
    }

    /** 分割字符串为List */
    public static List<String> split(String str, String delimiter) {
        if (isEmpty(str) || delimiter == null) return List.of();
        return Arrays.asList(str.split(Pattern.quote(delimiter)));
    }

    /** 连接字符串List为单个字符串 */
    public static String join(List<String> list, String delimiter) {
        if (list == null || list.isEmpty()) return "";
        return list.stream().collect(Collectors.joining(delimiter));
    }

    /** 首字母大写 */
    public static String capitalize(String str) {
        if (isEmpty(str)) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /** 首字母小写 */
    public static String uncapitalize(String str) {
        if (isEmpty(str)) return str;
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    /** 截取字符串，超长自动省略号 */
    public static String abbreviate(String str, int maxWidth) {
        if (str == null) return null;
        if (str.length() <= maxWidth) return str;
        return str.substring(0, Math.max(0, maxWidth - 3)) + "...";
    }

    /** 替换全部子串 */
    public static String replace(String str, String target, String replacement) {
        if (str == null || target == null || replacement == null) return str;
        return str.replace(target, replacement);
    }

    /** 正则匹配 */
    public static boolean matches(String str, String regex) {
        if (str == null || regex == null) return false;
        return Pattern.matches(regex, str);
    }

    /** 字符串格式化（占位符%s） */
    public static String format(String template, Object... args) {
        if (template == null) return null;
        return String.format(template, args);
    }
}