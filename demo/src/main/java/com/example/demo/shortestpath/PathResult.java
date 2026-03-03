package com.example.demo.shortestpath;

import java.util.*;

/**
 * 最短路径结果类
 * 存储从起点到终点的最短路径信息
 */
public class PathResult {
    private String start;
    private String end;
    private double distance;
    private List<String> path;
    private boolean pathExists;
    
    public PathResult(String start, String end) {
        this.start = start;
        this.end = end;
        this.distance = Double.POSITIVE_INFINITY;
        this.path = new ArrayList<>();
        this.pathExists = false;
    }
    
    public PathResult(String start, String end, double distance, List<String> path) {
        this.start = start;
        this.end = end;
        this.distance = distance;
        this.path = path;
        this.pathExists = !path.isEmpty();
    }
    
    public String getStart() {
        return start;
    }
    
    public String getEnd() {
        return end;
    }
    
    public double getDistance() {
        return distance;
    }
    
    public List<String> getPath() {
        return path;
    }
    
    public boolean isPathExists() {
        return pathExists;
    }
    
    /**
     * 格式化输出路径
     */
    public String getFormattedPath() {
        if (!pathExists) {
            return "无可达路径";
        }
        return String.join(" → ", path);
    }
    
    @Override
    public String toString() {
        if (!pathExists) {
            return String.format("从 %s 到 %s 没有可达路径", start, end);
        }
        return String.format("从 %s 到 %s 的最短路径:\n" +
                        "路径: %s\n" +
                        "总距离: %.2f",
                start, end, getFormattedPath(), distance);
    }
}
