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

    /**
     * 深拷贝对象（需实现Serializable接口）
     */
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

    /**
     * 生成指定长度的随机字符串
     */
    public static String randomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(length);
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /**
     * 判断字符串是否为邮箱
     */
    public static boolean isEmail(String str) {
        if (isEmpty(str)) return false;
        return str.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    /**
     * 判断字符串是否为手机号（中国大陆）
     */
    public static boolean isMobile(String str) {
        if (isEmpty(str)) return false;
        return str.matches("^1[3-9]\\d{9}$");
    }

    /**
     * List转逗号分隔字符串
     */
    public static String joinList(java.util.List<?> list) {
        if (list == null || list.isEmpty()) return "";
        return String.join(",", list.stream().map(String::valueOf).toArray(String[]::new));
    }

    /**
     * 逗号分隔字符串转List
     */
    public static java.util.List<String> splitToList(String str) {
        if (isEmpty(str)) return java.util.Collections.emptyList();
        String[] arr = str.split(",");
        java.util.List<String> list = new java.util.ArrayList<>();
        for (String s : arr) {
            list.add(s.trim());
        }
        return list;
    }

    /**
     * 获取UUID字符串
     */
    public static String uuid() {
        return java.util.UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 判断数组是否为空
     */
    public static boolean isEmptyArray(Object[] arr) {
        return arr == null || arr.length == 0;
    }

    /**
     * 获取当前时间戳（毫秒）
     */
    public static long currentTimestamp() {
        return System.currentTimeMillis();
    }

    /**
     * 对象转JSON字符串
     */
    public static String toJson(Object obj) {
        if (obj == null) return "";
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("JSON序列化失败", e);
        }
    }

    /**
     * JSON字符串转对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        if (isEmpty(json) || clazz == null) return null;
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("JSON反序列化失败", e);
        }
    }

    /**
     * 获取文件扩展名
     */
    public static String getFileExtension(String filename) {
        if (isEmpty(filename) || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf('.') + 1);
    }

    /**
     * 去除字符串首尾指定字符
     */
    public static String trim(String str, char ch) {
        if (isEmpty(str)) return str;
        int start = 0, end = str.length() - 1;
        while (start <= end && str.charAt(start) == ch) start++;
        while (end >= start && str.charAt(end) == ch) end--;
        return str.substring(start, end + 1);
    }

    /**
     * 判断是否为IP地址
     */
    public static boolean isIp(String str) {
        if (isEmpty(str)) return false;
        return str.matches("^(\\d{1,3}\\.){3}\\d{1,3}$");
    }

    /**
     * 获取本机IP地址
     */
    public static String getLocalIp() {
        try {
            java.net.InetAddress addr = java.net.InetAddress.getLocalHost();
            return addr.getHostAddress();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 判断是否为URL
     */
    public static boolean isUrl(String str) {
        if (isEmpty(str)) return false;
        return str.matches("^(http|https)://.*$");
    }

    /**
     * 获取指定范围随机整数
     */
    public static int randomInt(int min, int max) {
        if (min > max) throw new IllegalArgumentException("min不能大于max");
        return min + new java.util.Random().nextInt(max - min + 1);
    }
}
