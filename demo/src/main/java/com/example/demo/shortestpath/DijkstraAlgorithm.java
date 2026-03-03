package com.example.demo.shortestpath;

import java.util.*;

/**
 * Dijkstra最短路径算法实现
 * 用于计算单源最短路径问题
 * 
 * 算法思想：
 * 1. 初始化：设起点距离为0，其他所有节点距离为无穷大
 * 2. 从未访问节点中选择距离最小的节点
 * 3. 更新该节点所有邻居的距离（松弛操作）
 * 4. 标记该节点为已访问
 * 5. 重复步骤2-4直到所有节点被访问或目标节点被访问
 * 
 * 时间复杂度：O((V+E)logV)，其中V是节点数，E是边数
 * 空间复杂度：O(V)
 */
public class DijkstraAlgorithm {
    
    /**
     * 计算从起点到终点的最短路径
     * 
     * @param graph 图数据结构
     * @param start 起点
     * @param end 终点
     * @return 路径结果对象
     */
    public static PathResult findShortestPath(Graph graph, String start, String end) {
        // 验证起点和终点是否存在
        if (!graph.getNodes().contains(start) || !graph.getNodes().contains(end)) {
            return new PathResult(start, end);
        }
        
        // 距离表：存储从起点到每个节点的最短距离
        Map<String, Double> distances = new HashMap<>();
        // 前驱节点表：用于重建路径
        Map<String, String> previous = new HashMap<>();
        // 已访问节点集合
        Set<String> visited = new HashSet<>();
        
        // 优先队列：按距离排序，距离小的优先
        PriorityQueue<NodeDistance> pq = new PriorityQueue<>(
            Comparator.comparingDouble(NodeDistance::getDistance)
        );
        
        // 初始化：所有节点距离设为无穷大
        for (String node : graph.getNodes()) {
            distances.put(node, Double.POSITIVE_INFINITY);
        }
        
        // 起点距离设为0
        distances.put(start, 0.0);
        pq.offer(new NodeDistance(start, 0.0));
        
        // 主循环：处理优先队列中的节点
        while (!pq.isEmpty()) {
            NodeDistance current = pq.poll();
            String currentNode = current.getNode();
            
            // 如果已访问过，跳过
            if (visited.contains(currentNode)) {
                continue;
            }
            
            // 标记为已访问
            visited.add(currentNode);
            
            // 如果到达终点，可以提前结束
            if (currentNode.equals(end)) {
                break;
            }
            
            // 松弛操作：更新相邻节点的距离
            for (Graph.Edge edge : graph.getEdges(currentNode)) {
                String neighbor = edge.getDestination();
                
                // 如果邻居已访问，跳过
                if (visited.contains(neighbor)) {
                    continue;
                }
                
                // 计算通过当前节点到达邻居的距离
                double newDistance = distances.get(currentNode) + edge.getWeight();
                
                // 如果新距离更短，更新距离和前驱节点
                if (newDistance < distances.get(neighbor)) {
                    distances.put(neighbor, newDistance);
                    previous.put(neighbor, currentNode);
                    pq.offer(new NodeDistance(neighbor, newDistance));
                }
            }
        }
        
        // 重建路径
        List<String> path = reconstructPath(previous, start, end);
        double totalDistance = distances.get(end);
        
        return new PathResult(start, end, totalDistance, path);
    }
    
    /**
     * 计算从起点到所有节点的最短路径
     * 
     * @param graph 图数据结构
     * @param start 起点
     * @return 从起点到所有节点的路径结果映射
     */
    public static Map<String, PathResult> findAllShortestPaths(Graph graph, String start) {
        Map<String, PathResult> results = new HashMap<>();
        
        for (String node : graph.getNodes()) {
            if (!node.equals(start)) {
                results.put(node, findShortestPath(graph, start, node));
            }
        }
        
        return results;
    }
    
    /**
     * 从前驱节点表重建路径
     */
    private static List<String> reconstructPath(Map<String, String> previous, 
                                                 String start, String end) {
        List<String> path = new ArrayList<>();
        
        // 如果终点不可达
        if (!previous.containsKey(end) && !start.equals(end)) {
            return path;
        }
        
        // 从终点回溯到起点
        String current = end;
        while (current != null) {
            path.add(0, current);  // 在列表开头插入
            current = previous.get(current);
        }
        
        return path;
    }
    
    /**
     * 节点距离对，用于优先队列
     */
    private static class NodeDistance {
        private String node;
        private double distance;
        
        public NodeDistance(String node, double distance) {
            this.node = node;
            this.distance = distance;
        }
        
        public String getNode() {
            return node;
        }
        
        public double getDistance() {
            return distance;
        }
    }
}
