package com.example.demo;

import java.util.*;
import java.util.function.Consumer;

/**
 * 树遍历工具：提供前序 DFS（递归/非递归）与层序 BFS。
 * 使用泛型树节点结构 TreeTraversalUtils.Node<T>。
 *
 * 使用说明：
 * - Node<T> 为通用树节点，children 为子节点列表
 * - dfsPreOrder / dfsPreOrderStack：前序遍历（根 -> 子）
 * - bfsLevelOrder：按层返回节点值列表（自顶向下）
 * - dfsWithConsumer：遍历时执行回调函数
 *
 * 性能与复杂度：
 * - DFS/BFS 时间复杂度 O(N)，空间复杂度 O(N)（递归栈/队列/栈）
 *
 * 线程安全：
 * - 所有方法使用局部变量，无共享状态，线程安全
 *
 * 示例：
 * TreeTraversalUtils.Node<String> root = new TreeTraversalUtils.Node<>("A");
 * root.addChild(new TreeTraversalUtils.Node<>("B").addChild(new TreeTraversalUtils.Node<>("D")));
 * root.addChild(new TreeTraversalUtils.Node<>("C"));
 * List<String> pre = TreeTraversalUtils.dfsPreOrder(root);      // [A, B, D, C]
 * List<List<String>> levels = TreeTraversalUtils.bfsLevelOrder(root); // [[A], [B, C], [D]]
 */
public class TreeTraversalUtils {

    /**
     * 泛型树节点
     */
    public static class Node<T> {
        public T val;                        // 节点值
        public List<Node<T>> children = new ArrayList<>(); // 子节点列表

        public Node(T val) {
            this.val = val;
        }

        /**
         * 添加子节点，支持链式调用
         */
        public Node<T> addChild(Node<T> child) {
            this.children.add(child);
            return this;
        }
    }

    /**
     * 前序 DFS（递归），返回遍历顺序
     */
    /**
     * 前序 DFS（递归）
     * - 入参：根节点
     * - 出参：前序遍历序列（根 -> 子）
     */
    public static <T> List<T> dfsPreOrder(Node<T> root) {
        List<T> order = new ArrayList<>();
        dfs(root, order);
        return order;
    }

    /**
     * 前序 DFS 递归实现
     * - 若节点为空直接返回
     */
    private static <T> void dfs(Node<T> node, List<T> order) {
        if (node == null) return;
        order.add(node.val);
        for (Node<T> child : node.children) {
            dfs(child, order);
        }
    }

    /**
     * 前序 DFS（非递归，使用栈），返回遍历顺序
     */
    /**
     * 前序 DFS（非递归，使用栈）
     * - 逆序入栈，保证从左到右遍历
     */
    public static <T> List<T> dfsPreOrderStack(Node<T> root) {
        List<T> order = new ArrayList<>();
        if (root == null) return order;
        Deque<Node<T>> stack = new ArrayDeque<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            Node<T> node = stack.pop();
            order.add(node.val);
            // 逆序入栈，保证遍历从左到右
            for (int i = node.children.size() - 1; i >= 0; i--) {
                stack.push(node.children.get(i));
            }
        }
        return order;
    }

    /**
     * 层序 BFS，按层返回节点值
     */
    /**
     * 层序 BFS
     * - 每层形成一个列表，整体返回 List<List<T>>
     */
    public static <T> List<List<T>> bfsLevelOrder(Node<T> root) {
        List<List<T>> levels = new ArrayList<>();
        if (root == null) return levels;
        Deque<Node<T>> queue = new ArrayDeque<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            int size = queue.size();
            List<T> level = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                Node<T> node = queue.poll();
                level.add(node.val);
                for (Node<T> child : node.children) {
                    queue.offer(child);
                }
            }
            levels.add(level);
        }
        return levels;
    }

    /**
     * 前序 DFS（非递归），遍历时执行回调
     */
    /**
     * 前序 DFS（非递归），遍历时执行回调
     * - 适合需要对节点值进行处理的场景
     */
    public static <T> void dfsWithConsumer(Node<T> root, Consumer<T> consumer) {
        if (root == null || consumer == null) return;
        Deque<Node<T>> stack = new ArrayDeque<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            Node<T> node = stack.pop();
            consumer.accept(node.val);
            for (int i = node.children.size() - 1; i >= 0; i--) {
                stack.push(node.children.get(i));
            }
        }
    }
}