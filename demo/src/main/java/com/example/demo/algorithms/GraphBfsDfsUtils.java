package com.example.demo.algorithms;

import java.util.*;

/**
 * 通用的图与树的 BFS/DFS 工具类。
 * 说明：
 * - 图遍历：基于邻接表（Map&lt;T, List&lt;T&gt;&gt;），支持有向/无向图（由邻接表构造者决定）。
 * - 树遍历：提供最小的 TreeNode 定义（泛型），支持前序/中序/后序/层序等。
 * - 所有方法均为静态、无状态、非线程安全（但通常满足使用需求）。
 */
public final class GraphBfsDfsUtils {

    private GraphBfsDfsUtils() {
    }

    // =========================
    // 图遍历（BFS / DFS）
    // =========================

    /**
     * 图的 BFS，返回从起点开始的访问顺序。
     */
    public static &lt;T&gt; List&lt;T&gt; graphBfs(Map&lt;T, List&lt;T&gt;&gt; adj, T start) {
        List&lt;T&gt; order = new ArrayList&lt;&gt;();
        if (start == null || adj == null || adj.isEmpty()) return order;

        Set&lt;T&gt; visited = new HashSet&lt;&gt;();
        Deque&lt;T&gt; queue = new ArrayDeque&lt;&gt;();

        visited.add(start);
        queue.offer(start);

        while (!queue.isEmpty()) {
            T cur = queue.poll();
            order.add(cur);

            List&lt;T&gt; neighbors = adj.getOrDefault(cur, Collections.emptyList());
            for (T nb : neighbors) {
                if (nb == null) continue;
                if (!visited.contains(nb)) {
                    visited.add(nb);
                    queue.offer(nb);
                }
            }
        }
        return order;
    }

    /**
     * 图的 DFS（迭代版），返回从起点开始的访问顺序。
     * 使用栈模拟递归，邻接表遍历时如需与递归顺序更一致，建议将邻接点逆序压栈。
     */
    public static &lt;T&gt; List&lt;T&gt; graphDfsIterative(Map&lt;T, List&lt;T&gt;&gt; adj, T start) {
        List&lt;T&gt; order = new ArrayList&lt;&gt;();
        if (start == null || adj == null || adj.isEmpty()) return order;

        Set&lt;T&gt; visited = new HashSet&lt;&gt;();
        Deque&lt;T&gt; stack = new ArrayDeque&lt;&gt;();

        stack.push(start);
        while (!stack.isEmpty()) {
            T cur = stack.pop();
            if (visited.contains(cur)) continue;
            visited.add(cur);
            order.add(cur);

            List&lt;T&gt; neighbors = adj.getOrDefault(cur, Collections.emptyList());
            // 为尽量模拟递归（先访问低索引/前置元素），这里逆序入栈
            for (int i = neighbors.size() - 1; i &gt;= 0; i--) {
                T nb = neighbors.get(i);
                if (nb != null &amp;&amp; !visited.contains(nb)) {
                    stack.push(nb);
                }
            }
        }
        return order;
    }

    /**
     * 图的 DFS（递归版），返回从起点开始的访问顺序。
     */
    public static &lt;T&gt; List&lt;T&gt; graphDfsRecursive(Map&lt;T, List&lt;T&gt;&gt; adj, T start) {
        List&lt;T&gt; order = new ArrayList&lt;&gt;();
        if (start == null || adj == null || adj.isEmpty()) return order;
        Set&lt;T&gt; visited = new HashSet&lt;&gt;();
        dfsHelper(adj, start, visited, order);
        return order;
    }

    private static &lt;T&gt; void dfsHelper(Map&lt;T, List&lt;T&gt;&gt; adj, T node, Set&lt;T&gt; visited, List&lt;T&gt; order) {
        if (node == null || visited.contains(node)) return;
        visited.add(node);
        order.add(node);
        for (T nb : adj.getOrDefault(node, Collections.emptyList())) {
            if (nb != null &amp;&amp; !visited.contains(nb)) {
                dfsHelper(adj, nb, visited, order);
            }
        }
    }

    /**
     * 无起点的全图 BFS（适用于非连通图），返回所有连通分量的遍历序列拼接。
     */
    public static &lt;T&gt; List&lt;T&gt; graphBfsAll(Map&lt;T, List&lt;T&gt;&gt; adj) {
        List&lt;T&gt; order = new ArrayList&lt;&gt;();
        if (adj == null || adj.isEmpty()) return order;

        Set&lt;T&gt; visited = new HashSet&lt;&gt;();
        Deque&lt;T&gt; queue = new ArrayDeque&lt;&gt;();

        for (T start : adj.keySet()) {
            if (start == null || visited.contains(start)) continue;
            visited.add(start);
            queue.offer(start);
            while (!queue.isEmpty()) {
                T cur = queue.poll();
                order.add(cur);
                for (T nb : adj.getOrDefault(cur, Collections.emptyList())) {
                    if (nb == null) continue;
                    if (!visited.contains(nb)) {
                        visited.add(nb);
                        queue.offer(nb);
                    }
                }
            }
        }
        return order;
    }

    /**
     * 无起点的全图 DFS（适用于非连通图，迭代版）。
     */
    public static &lt;T&gt; List&lt;T&gt; graphDfsAllIterative(Map&lt;T, List&lt;T&gt;&gt; adj) {
        List&lt;T&gt; order = new ArrayList&lt;&gt;();
        if (adj == null || adj.isEmpty()) return order;

        Set&lt;T&gt; visited = new HashSet&lt;&gt;();

        for (T start : adj.keySet()) {
            if (start == null || visited.contains(start)) continue;
            Deque&lt;T&gt; stack = new ArrayDeque&lt;&gt;();
            stack.push(start);
            while (!stack.isEmpty()) {
                T cur = stack.pop();
                if (visited.contains(cur)) continue;
                visited.add(cur);
                order.add(cur);
                List&lt;T&gt; neighbors = adj.getOrDefault(cur, Collections.emptyList());
                for (int i = neighbors.size() - 1; i &gt;= 0; i--) {
                    T nb = neighbors.get(i);
                    if (nb != null &amp;&amp; !visited.contains(nb)) {
                        stack.push(nb);
                    }
                }
            }
        }
        return order;
    }

    // =========================
    // 树遍历（BFS / DFS）
    // =========================

    /**
     * 最小二叉树节点定义（泛型）。
     */
    public static class TreeNode&lt;T&gt; {
        public T val;
        public TreeNode&lt;T&gt; left;
        public TreeNode&lt;T&gt; right;

        public TreeNode(T val) {
            this.val = val;
        }

        public TreeNode(T val, TreeNode&lt;T&gt; left, TreeNode&lt;T&gt; right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }

    /**
     * 树的 BFS（层序遍历），返回访问顺序。
     */
    public static &lt;T&gt; List&lt;T&gt; treeBfs(TreeNode&lt;T&gt; root) {
        List&lt;T&gt; order = new ArrayList&lt;&gt;();
        if (root == null) return order;

        Deque&lt;TreeNode&lt;T&gt;&gt; queue = new ArrayDeque&lt;&gt;();
        queue.offer(root);
        while (!queue.isEmpty()) {
            TreeNode&lt;T&gt; cur = queue.poll();
            order.add(cur.val);
            if (cur.left != null) queue.offer(cur.left);
            if (cur.right != null) queue.offer(cur.right);
        }
        return order;
    }

    /**
     * 树的层序分层遍历，返回每一层的节点列表。
     */
    public static &lt;T&gt; List&lt;List&lt;T&gt;&gt; treeLevelOrder(TreeNode&lt;T&gt; root) {
        List&lt;List&lt;T&gt;&gt; levels = new ArrayList&lt;&gt;();
        if (root == null) return levels;

        Deque&lt;TreeNode&lt;T&gt;&gt; queue = new ArrayDeque&lt;&gt;();
        queue.offer(root);
        while (!queue.isEmpty()) {
            int size = queue.size();
            List&lt;T&gt; layer = new ArrayList&lt;&gt;(size);
            for (int i = 0; i &lt; size; i++) {
                TreeNode&lt;T&gt; cur = queue.poll();
                layer.add(cur.val);
                if (cur.left != null) queue.offer(cur.left);
                if (cur.right != null) queue.offer(cur.right);
            }
            levels.add(layer);
        }
        return levels;
    }

    /**
     * 树的前序 DFS（递归版）。
     */
    public static &lt;T&gt; List&lt;T&gt; treePreOrderRecursive(TreeNode&lt;T&gt; root) {
        List&lt;T&gt; order = new ArrayList&lt;&gt;();
        preOrderDfs(root, order);
        return order;
    }

    private static &lt;T&gt; void preOrderDfs(TreeNode&lt;T&gt; node, List&lt;T&gt; order) {
        if (node == null) return;
        order.add(node.val);
        preOrderDfs(node.left, order);
        preOrderDfs(node.right, order);
    }

    /**
     * 树的中序 DFS（递归版）。
     */
    public static &lt;T&gt; List&lt;T&gt; treeInOrderRecursive(TreeNode&lt;T&gt; root) {
        List&lt;T&gt; order = new ArrayList&lt;&gt;();
        inOrderDfs(root, order);
        return order;
    }

    private static &lt;T&gt; void inOrderDfs(TreeNode&lt;T&gt; node, List&lt;T&gt; order) {
        if (node == null) return;
        inOrderDfs(node.left, order);
        order.add(node.val);
        inOrderDfs(node.right, order);
    }

    /**
     * 树的后序 DFS（递归版）。
     */
    public static &lt;T&gt; List&lt;T&gt; treePostOrderRecursive(TreeNode&lt;T&gt; root) {
        List&lt;T&gt; order = new ArrayList&lt;&gt;();
        postOrderDfs(root, order);
        return order;
    }

    private static &lt;T&gt; void postOrderDfs(TreeNode&lt;T&gt; node, List&lt;T&gt; order) {
        if (node == null) return;
        postOrderDfs(node.left, order);
        postOrderDfs(node.right, order);
        order.add(node.val);
    }

    /**
     * 树的前序 DFS（迭代版）。
     */
    public static &lt;T&gt; List&lt;T&gt; treePreOrderIterative(TreeNode&lt;T&gt; root) {
        List&lt;T&gt; order = new ArrayList&lt;&gt;();
        if (root == null) return order;
        Deque&lt;TreeNode&lt;T&gt;&gt; stack = new ArrayDeque&lt;&gt;();
        stack.push(root);
        while (!stack.isEmpty()) {
            TreeNode&lt;T&gt; cur = stack.pop();
            order.add(cur.val);
            if (cur.right != null) stack.push(cur.right);
            if (cur.left != null) stack.push(cur.left);
        }
        return order;
    }

    /**
     * 树的中序 DFS（迭代版）。
     */
    public static &lt;T&gt; List&lt;T&gt; treeInOrderIterative(TreeNode&lt;T&gt; root) {
        List&lt;T&gt; order = new ArrayList&lt;&gt;();
        Deque&lt;TreeNode&lt;T&gt;&gt; stack = new ArrayDeque&lt;&gt;();
        TreeNode&lt;T&gt; cur = root;
        while (cur != null || !stack.isEmpty()) {
            while (cur != null) {
                stack.push(cur);
                cur = cur.left;
            }
            cur = stack.pop();
            order.add(cur.val);
            cur = cur.right;
        }
        return order;
    }

    /**
     * 树的后序 DFS（迭代版），双栈或单栈标记法，这里使用单栈+指针回溯。
     */
    public static &lt;T&gt; List&lt;T&gt; treePostOrderIterative(TreeNode&lt;T&gt; root) {
        List&lt;T&gt; order = new ArrayList&lt;&gt;();
        Deque&lt;TreeNode&lt;T&gt;&gt; stack = new ArrayDeque&lt;&gt;();
        TreeNode&lt;T&gt; cur = root, last = null;
        while (cur != null || !stack.isEmpty()) {
            if (cur != null) {
                stack.push(cur);
                cur = cur.left;
            } else {
                TreeNode&lt;T&gt; peek = stack.peek();
                if (peek.right != null &amp;&amp; last != peek.right) {
                    cur = peek.right;
                } else {
                    order.add(peek.val);
                    last = stack.pop();
                }
            }
        }
        return order;
    }
}