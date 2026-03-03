package com.example.demo.shortestpath;

import java.util.*;

/**
 * 图的数据结构
 * 用于表示带权重的有向图或无向图
 */
public class Graph {
    // 邻接表存储图，key为节点，value为该节点的所有邻接边
    private Map<String, List<Edge>> adjacencyList;
    
    public Graph() {
        this.adjacencyList = new HashMap<>();
    }
    
    /**
     * 添加节点
     */
    public void addNode(String node) {
        adjacencyList.putIfAbsent(node, new ArrayList<>());
    }
    
    /**
     * 添加边（有向图）
     */
    public void addEdge(String from, String to, double weight) {
        adjacencyList.putIfAbsent(from, new ArrayList<>());
        adjacencyList.get(from).add(new Edge(to, weight));
    }
    
    /**
     * 添加双向边（无向图）
     */
    public void addUndirectedEdge(String node1, String node2, double weight) {
        addEdge(node1, node2, weight);
        addEdge(node2, node1, weight);
    }
    
    /**
     * 获取某个节点的所有邻接边
     */
    public List<Edge> getEdges(String node) {
        return adjacencyList.getOrDefault(node, new ArrayList<>());
    }
    
    /**
     * 获取所有节点
     */
    public Set<String> getNodes() {
        return adjacencyList.keySet();
    }
    
    /**
     * 边的内部类
     */
    public static class Edge {
        private String destination;
        private double weight;
        
        public Edge(String destination, double weight) {
            this.destination = destination;
            this.weight = weight;
        }
        
        public String getDestination() {
            return destination;
        }
        
        public double getWeight() {
            return weight;
        }
        
        @Override
        public String toString() {
            return "Edge{" +
                    "destination='" + destination + '\'' +
                    ", weight=" + weight +
                    '}';
        }
    }
}
