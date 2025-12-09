package com.example.demo;

import java.util.*;

/**
 * 图遍历工具：提供 DFS（递归/非递归）与 BFS（层序、最短路径）。
 * 适用于邻接表表示的有向/无向图：Map<T, List<T>>。
 *
 * 使用说明：
 * - 传入 graph：节点到邻居列表的映射；传入 start：起始节点
 * - 含环图请务必使用 visited 集合避免死循环（内部已处理）
 * - bfsShortestPath 适用于无权图最短路径（每条边权重相同）
 *
 * 性能与复杂度：
 * - DFS/BFS 时间复杂度 O(V + E)，空间复杂度 O(V)（visited、递归栈/队列）
 * - 递归 DFS 可能在深图上触发栈溢出，建议使用 dfsStack
 *
 * 线程安全：
 * - 方法内部使用局部集合（visited/order），无共享状态，线程安全
 *
 * 示例：
 * Map<Integer, List<Integer>> g = Map.of(
 *   1, List.of(2, 3),
 *   2, List.of(4),
 *   3, List.of(4),
 *   4, List.of()
 * );
 * List<Integer> dfs = GraphTraversalUtils.dfsRecursive(g, 1);
 * List<Integer> bfs = GraphTraversalUtils.bfs(g, 1);
 * List<Integer> path = GraphTraversalUtils.bfsShortestPath(g, 1, 4);
 */
public class GraphTraversalUtils {

    /**
     * 递归 DFS，返回遍历顺序
     */
    /**
     * 递归 DFS
     * - 入参：图（邻接表）、起点
     * - 出参：遍历顺序列表
     * - 注意：深度过大时可能栈溢出；可使用 dfsStack 替代
     */
    public static <T> List<T> dfsRecursive(Map<T, List<T>> graph, T start) {
        List<T> order = new ArrayList<>();
        Set<T> visited = new HashSet<>();
        dfs(graph, start, visited, order);
        return order;
    }

    /**
     * 递归 DFS 的具体实现
     * - visited：记录已访问节点，避免重复/环路
     * - order：遍历结果
     */
    private static <T> void dfs(Map<T, List<T>> graph, T node, Set<T> visited, List<T> order) {
        if (node == null || visited.contains(node)) return;
        visited.add(node);
        order.add(node);
        List<T> neighbors = graph.getOrDefault(node, Collections.emptyList());
        for (T nei : neighbors) {
            dfs(graph, nei, visited, order);
        }
    }

    /**
     * 非递归 DFS（使用栈），返回遍历顺序
     */
    /**
     * 非递归 DFS（栈）
     * - 使用栈模拟递归调用
     * - 为了近似递归顺序，邻居逆序入栈
     */
    public static <T> List<T> dfsStack(Map<T, List<T>> graph, T start) {
        List<T> order = new ArrayList<>();
        if (start == null) return order;
        Set<T> visited = new HashSet<>();
        Deque<T> stack = new ArrayDeque<>();
        stack.push(start);
        while (!stack.isEmpty()) {
            T node = stack.pop();
            if (visited.contains(node)) continue;
            visited.add(node);
            order.add(node);
            List<T> neighbors = graph.getOrDefault(node, Collections.emptyList());
            // 为了更接近递归的顺序，逆序入栈
            for (int i = neighbors.size() - 1; i >= 0; i--) {
                T nei = neighbors.get(i);
                if (!visited.contains(nei)) {
                    stack.push(nei);
                }
            }
        }
        return order;
    }

    /**
     * BFS 层序遍历，返回遍历顺序
     */
    /**
     * BFS（队列）
     * - 层序遍历图，适用于求最短步数问题的基础
     * - visited 先标记避免重复入队
     */
    public static <T> List<T> bfs(Map<T, List<T>> graph, T start) {
        List<T> order = new ArrayList<>();
        if (start == null) return order;
        Set<T> visited = new HashSet<>();
        Deque<T> queue = new ArrayDeque<>();
        queue.offer(start);
        visited.add(start);
        while (!queue.isEmpty()) {
            T node = queue.poll();
            order.add(node);
            List<T> neighbors = graph.getOrDefault(node, Collections.emptyList());
            for (T nei : neighbors) {
                if (!visited.contains(nei)) {
                    visited.add(nei);
                    queue.offer(nei);
                }
            }
        }
        return order;
    }

    /**
     * BFS 最短路径（无权图），返回从 start 到 target 的路径（若不存在返回空列表）
     */
    /**
     * BFS 最短路径（无权图）
     * - 通过 prev 记录每个节点的前驱，最终回溯构建路径
     * - 若不可达返回空列表
     */
    public static <T> List<T> bfsShortestPath(Map<T, List<T>> graph, T start, T target) {
        if (start == null || target == null) return Collections.emptyList();
        Map<T, T> prev = new HashMap<>();
        Set<T> visited = new HashSet<>();
        Deque<T> queue = new ArrayDeque<>();
        queue.offer(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            T node = queue.poll();
            if (node.equals(target)) {
                return reconstructPath(prev, start, target);
            }
            for (T nei : graph.getOrDefault(node, Collections.emptyList())) {
                if (!visited.contains(nei)) {
                    visited.add(nei);
                    prev.put(nei, node);
                    queue.offer(nei);
                }
            }
        }
        return Collections.emptyList();
    }

    /**
     * 根据前驱映射重建路径
     * - 从 target 反向走到 start
     * - 反转得到正向路径
     */
    private static <T> List<T> reconstructPath(Map<T, T> prev, T start, T target) {
        List<T> path = new ArrayList<>();
        T cur = target;
        while (cur != null) {
            path.add(cur);
            if (cur.equals(start)) break;
            cur = prev.get(cur);
        }
        Collections.reverse(path);
        return path.isEmpty() || !path.get(0).equals(start) ? Collections.emptyList() : path;
    }
}