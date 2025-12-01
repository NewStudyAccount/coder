package com.example.demo;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 服务器信息工具类，提供主机、系统、JVM等相关信息获取，全部为静态方法。
 */
public class ServerInfoUtils {

    /** 获取主机名 */
    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            System.err.println("getHostName error: " + e.getMessage());
            return "unknown";
        }
    }

    /** 获取本机IP地址 */
    public static String getHostAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            System.err.println("getHostAddress error: " + e.getMessage());
            return "unknown";
        }
    }

    /** 获取操作系统名称 */
    public static String getOsName() {
        return System.getProperty("os.name");
    }

    /** 获取操作系统版本 */
    public static String getOsVersion() {
        return System.getProperty("os.version");
    }

    /** 获取操作系统架构 */
    public static String getOsArch() {
        return System.getProperty("os.arch");
    }

    /** 获取CPU核心数 */
    public static int getCpuCoreCount() {
        return Runtime.getRuntime().availableProcessors();
    }

    /** 获取JVM内存总量（MB） */
    public static long getJvmTotalMemoryMB() {
        return Runtime.getRuntime().totalMemory() / (1024 * 1024);
    }

    /** 获取JVM最大可用内存（MB） */
    public static long getJvmMaxMemoryMB() {
        return Runtime.getRuntime().maxMemory() / (1024 * 1024);
    }

    /** 获取JVM已用内存（MB） */
    public static long getJvmUsedMemoryMB() {
        return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024);
    }

    /** 获取JVM启动参数 */
    public static String getJvmArgs() {
        RuntimeMXBean mxBean = ManagementFactory.getRuntimeMXBean();
        if (mxBean.getInputArguments() == null || mxBean.getInputArguments().isEmpty()) {
            return "";
        }
        return String.join(" ", mxBean.getInputArguments());
    }

    /** 获取JVM版本 */
    public static String getJvmVersion() {
        return System.getProperty("java.version");
    }

    /** 获取JVM供应商 */
    public static String getJvmVendor() {
        return System.getProperty("java.vendor");
    }
}