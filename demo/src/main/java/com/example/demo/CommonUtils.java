package com.example.demo;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 通用工具类，封装常用静态方法，建议全项目统一使用
 */
public class CommonUtils {

    /** 判断字符串是否为空（null/空串/全空格） */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /** 判断字符串是否非空 */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /** 判断集合是否为空 */
    public static boolean isEmpty(Collection<?> coll) {
        return coll == null || coll.isEmpty();
    }

    /** 判断Map是否为空 */
    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /** 判断数组是否为空 */
    public static boolean isEmptyArray(Object[] arr) {
        return arr == null || arr.length == 0;
    }

    /** 格式化日期为字符串（线程安全） */
    public static String formatDate(Date date, String pattern) {
        if (date == null || isEmpty(pattern)) return "";
        return java.time.format.DateTimeFormatter.ofPattern(pattern)
                .format(date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());
    }

    /** 安全地将对象转为字符串 */
    public static String toString(Object obj) {
        return obj == null ? "" : obj.toString();
    }

    /** 判断字符串是否为数字 */
    private static final Pattern NUMERIC_PATTERN = Pattern.compile("\\d+");
    public static boolean isNumeric(String str) {
        if (isEmpty(str)) return false;
        return NUMERIC_PATTERN.matcher(str).matches();
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

    /** 字符串反转 */
    public static String reverse(String str) {
        if (str == null) return null;
        return new StringBuilder(str).reverse().toString();
    }

    /** 分割字符串为List（支持任意分隔符） */
    public static List<String> split(String str, String delimiter) {
        if (isEmpty(str) || delimiter == null) return Collections.emptyList();
        return Arrays.asList(str.split(Pattern.quote(delimiter)));
    }

    /** 连接字符串List为单个字符串 */
    public static String join(List<?> list, String delimiter) {
        if (list == null || list.isEmpty()) return "";
        return list.stream().map(String::valueOf).collect(Collectors.joining(delimiter));
    }

    /** 字符串格式化（占位符%s） */
    public static String format(String template, Object... args) {
        if (template == null) return null;
        return String.format(template, args);
    }

    /** List转逗号分隔字符串 */
    public static String joinList(List<?> list) {
        return join(list, ",");
    }

    /** 逗号分隔字符串转List */
    public static List<String> splitToList(String str) {
        return split(str, ",").stream().map(String::trim).collect(Collectors.toList());
    }

    /** 深拷贝对象（需实现Serializable接口） */
    @SuppressWarnings("unchecked")
    public static <T> T deepClone(T obj) {
        if (obj == null) return null;
        try {
            java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
            java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(bos.toByteArray());
            java.io.ObjectInputStream ois = new java.io.ObjectInputStream(bis);
            return (T) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException("深拷贝失败", e);
        }
    }

    /** 生成指定长度的随机字符串 */
    private static final String RANDOM_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final Random GLOBAL_RANDOM = new Random();
    public static String randomString(int length) {
        if (length <= 0) return "";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(RANDOM_CHARS.charAt(GLOBAL_RANDOM.nextInt(RANDOM_CHARS.length())));
        }
        return sb.toString();
    }

    /** 判断字符串是否为邮箱 */
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    public static boolean isEmail(String str) {
        if (isEmpty(str)) return false;
        return EMAIL_PATTERN.matcher(str).matches();
    }

    /** 判断字符串是否为手机号（中国大陆） */
    private static final Pattern MOBILE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    public static boolean isMobile(String str) {
        if (isEmpty(str)) return false;
        return MOBILE_PATTERN.matcher(str).matches();
    }

    /** 获取UUID字符串 */
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /** 获取文件扩展名 */
    public static String getFileExtension(String filename) {
        if (isEmpty(filename) || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf('.') + 1);
    }

    /** 去除字符串首尾指定字符 */
    public static String trim(String str, char ch) {
        if (isEmpty(str)) return str;
        int start = 0, end = str.length() - 1;
        while (start <= end && str.charAt(start) == ch) start++;
        while (end >= start && str.charAt(end) == ch) end--;
        return str.substring(start, end + 1);
    }

    /** 判断是否为IP地址 */
    private static final Pattern IP_PATTERN = Pattern.compile("^(\\d{1,3}\\.){3}\\d{1,3}$");
    public static boolean isIp(String str) {
        if (isEmpty(str)) return false;
        return IP_PATTERN.matcher(str).matches();
    }

    /** 获取本机IP地址 */
    public static String getLocalIp() {
        try {
            java.net.InetAddress addr = java.net.InetAddress.getLocalHost();
            return addr.getHostAddress();
        } catch (Exception e) {
            return "";
        }
    }

    /** 判断是否为URL */
    private static final Pattern URL_PATTERN = Pattern.compile("^(http|https)://.*$");
    public static boolean isUrl(String str) {
        if (isEmpty(str)) return false;
        return URL_PATTERN.matcher(str).matches();
    }

    /** 获取指定范围随机整数 */
    public static int randomInt(int min, int max) {
        if (min > max) throw new IllegalArgumentException("min不能大于max");
        return min + GLOBAL_RANDOM.nextInt(max - min + 1);
    }

    /** 对象转JSON字符串 */
    public static String toJson(Object obj) {
        if (obj == null) return "";
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("JSON序列化失败: " + e.getMessage(), e);
        }
    }

    /** JSON字符串转对象 */
    public static <T> T fromJson(String json, Class<T> clazz) {
        if (isEmpty(json) || clazz == null) return null;
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("JSON反序列化失败", e);
        }
    }
}