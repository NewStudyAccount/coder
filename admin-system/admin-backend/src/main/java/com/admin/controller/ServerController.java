package com.admin.controller;

import com.admin.common.result.Result;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/monitor/server")
public class ServerController {

    @GetMapping("/info")
    @PreAuthorize("hasAuthority('monitor:server:list')")
    public Result<Map<String, Object>> serverInfo() {
        Map<String, Object> info = new HashMap<>();

        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        info.put("osName", osBean.getName());
        info.put("osArch", osBean.getArch());
        info.put("osVersion", osBean.getVersion());
        info.put("processors", osBean.getAvailableProcessors());

        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        long totalPhysical = memoryBean.getHeapMemoryUsage().getMax() + memoryBean.getNonHeapMemoryUsage().getMax();
        long usedHeap = memoryBean.getHeapMemoryUsage().getUsed();
        long maxHeap = memoryBean.getHeapMemoryUsage().getMax();
        long usedNonHeap = memoryBean.getNonHeapMemoryUsage().getUsed();

        Map<String, Object> jvm = new HashMap<>();
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        jvm.put("name", runtimeBean.getVmName());
        jvm.put("version", runtimeBean.getSpecVersion());
        jvm.put("startTime", runtimeBean.getStartTime());
        jvm.put("uptime", runtimeBean.getUptime());
        jvm.put("usedHeap", usedHeap);
        jvm.put("maxHeap", maxHeap);
        jvm.put("heapUsage", maxHeap > 0 ? String.format("%.1f%%", (double) usedHeap / maxHeap * 100) : "N/A");
        jvm.put("usedNonHeap", usedNonHeap);
        info.put("jvm", jvm);

        Runtime runtime = Runtime.getRuntime();
        info.put("totalMemory", runtime.totalMemory());
        info.put("freeMemory", runtime.freeMemory());
        info.put("maxMemory", runtime.maxMemory());
        info.put("usedMemory", runtime.totalMemory() - runtime.freeMemory());
        info.put("memoryUsage", String.format("%.1f%%",
                (double) (runtime.totalMemory() - runtime.freeMemory()) / runtime.maxMemory() * 100));

        File[] roots = File.listRoots();
        java.util.List<Map<String, Object>> diskList = new java.util.ArrayList<>();
        for (File root : roots) {
            Map<String, Object> disk = new HashMap<>();
            disk.put("path", root.getAbsolutePath());
            disk.put("total", root.getTotalSpace());
            disk.put("free", root.getFreeSpace());
            disk.put("used", root.getTotalSpace() - root.getFreeSpace());
            disk.put("usage", root.getTotalSpace() > 0
                    ? String.format("%.1f%%", (double) (root.getTotalSpace() - root.getFreeSpace()) / root.getTotalSpace() * 100)
                    : "N/A");
            diskList.add(disk);
        }
        info.put("disks", diskList);

        return Result.success(info);
    }
}
