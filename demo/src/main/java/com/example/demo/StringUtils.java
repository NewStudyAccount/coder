package com.example.demo;

import java.util.List;
import java.util.regex.Pattern;

/**
 * 字符串处理工具类，仅保留专用字符串操作，基础方法请统一使用 CommonUtils
 */
public class StringUtils {

    /**
     * 截取字符串，超长自动省略号
     * @param str 原字符串
     * @param maxWidth 最大长度（必须大于3，否则返回"..."或null）
     * @return 截取后的字符串
     */
    public static String abbreviate(String str, int maxWidth) {
        if (str == null) return null;
        if (maxWidth <= 3) return str == null ? null : "...";
        if (str.length() <= maxWidth) return str;
        return str.substring(0, Math.max(0, maxWidth - 3)) + "...";
    }

    /**
     * 替换全部子串
     * @param str 原字符串
     * @param target 目标子串（允许为空串，表示插入replacement到每个字符间）
     * @param replacement 替换内容（允许为空串）
     * @return 替换后的字符串
     */
    public static String replace(String str, String target, String replacement) {
        if (str == null || target == null || replacement == null) return str;
        if (target.isEmpty()) {
            // target为空串，插入replacement到每个字符间
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < str.length(); i++) {
                sb.append(str.charAt(i));
                if (i < str.length() - 1) sb.append(replacement);
            }
            return sb.toString();
        }
        return str.replace(target, replacement);
    }

    /**
     * 正则匹配
     * @param str 待匹配字符串
     * @param regex 正则表达式
     * @return 是否匹配
     */
    public static boolean matches(String str, String regex) {
        if (str == null || regex == null) return false;
        return Pattern.matches(regex, str);
    }

    // 推荐使用 CommonUtils 的基础方法，如：
    // isEmpty、isNotEmpty、capitalize、uncapitalize、reverse、join、split、format 等
    // 例如：CommonUtils.isEmpty(str)
}