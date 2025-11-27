package com.example.demo;

import java.util.List;
import java.util.regex.Pattern;

/**
 * 字符串处理工具类，仅保留专用字符串操作，基础方法请统一使用 CommonUtils
 */
public class StringUtils {

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

    // 推荐使用 CommonUtils 的基础方法，如：
    // isEmpty、isNotEmpty、capitalize、uncapitalize、reverse、join、split、format 等
    // 例如：CommonUtils.isEmpty(str)
}