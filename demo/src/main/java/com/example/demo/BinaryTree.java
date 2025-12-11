package com.example.demo;

import java.util.*;

/**
 * 二叉树基础工具类：节点定义、递归/迭代遍历、层序构建与打印、深度与平衡判断。
 * 适合在单元测试或 main 方法中直接调用。
 */
public class BinaryTree {

    public static class TreeNode<T> {
        public T val;
        public TreeNode<T> left;
        public TreeNode<T> right;

        public TreeNode(T val) {
            this.val = val;
        }
    }

    // 构建：根据层序数组（null 表示空节点）构建二叉树
    public static <T> TreeNode<T> fromLevelOrder(List<T> values) {
        if (values == null || values.isEmpty() || values.get(0) == null) return null;
        TreeNode<T> root = new TreeNode<>(values.get(0));
        Queue<TreeNode<T>> queue = new LinkedList<>();
        queue.offer(root);
        int i = 1;
        while (!queue.isEmpty() && i < values.size()) {
            TreeNode<T> cur = queue.poll();
            // 左子
            if (i < values.size()) {
                T lv = values.get(i++);
                if (lv != null) {
                    cur.left = new TreeNode<>(lv);
                    queue.offer(cur.left);
                }
            }
            // 右子
            if (i < values.size()) {
                T rv = values.get(i++);
                if (rv != null) {
                    cur.right = new TreeNode<>(rv);
                    queue.offer(cur.right);
                }
            }
        }
        return root;
    }

    // 将树打印为层序列表（包含 null 占位，便于观察结构）
    public static <T> List<T> toLevelOrder(TreeNode<T> root) {
        List<T> res = new ArrayList<>();
        if (root == null) return res;
        Queue<TreeNode<T>> queue = new LinkedList<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            TreeNode<T> node = queue.poll();
            if (node == null) {
                res.add(null);
                continue;
            }
            res.add(node.val);
            queue.offer(node.left);
            queue.offer(node.right);
        }
        // 去除末尾多余的 null
        int end = res.size() - 1;
        while (end >= 0 && res.get(end) == null) end--;
        return res.subList(0, end + 1);
    }

    // 递归遍历
    public static <T> List<T> preorder(TreeNode<T> root) {
        List<T> res = new ArrayList<>();
        preorderDfs(root, res);
        return res;
    }

    private static <T> void preorderDfs(TreeNode<T> node, List<T> res) {
        if (node == null) return;
        res.add(node.val);
        preorderDfs(node.left, res);
        preorderDfs(node.right, res);
    }

    public static <T> List<T> inorder(TreeNode<T> root) {
        List<T> res = new ArrayList<>();
        inorderDfs(root, res);
        return res;
    }

    private static <T> void inorderDfs(TreeNode<T> node, List<T> res) {
        if (node == null) return;
        inorderDfs(node.left, res);
        res.add(node.val);
        inorderDfs(node.right, res);
    }

    public static <T> List<T> postorder(TreeNode<T> root) {
        List<T> res = new ArrayList<>();
        postorderDfs(root, res);
        return res;
    }

    private static <T> void postorderDfs(TreeNode<T> node, List<T> res) {
        if (node == null) return;
        postorderDfs(node.left, res);
        postorderDfs(node.right, res);
        res.add(node.val);
    }

    // 迭代遍历
    public static <T> List<T> preorderIter(TreeNode<T> root) {
        List<T> res = new ArrayList<>();
        if (root == null) return res;
        Deque<TreeNode<T>> stack = new ArrayDeque<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            TreeNode<T> node = stack.pop();
            res.add(node.val);
            if (node.right != null) stack.push(node.right);
            if (node.left != null) stack.push(node.left);
        }
        return res;
    }

    public static <T> List<T> inorderIter(TreeNode<T> root) {
        List<T> res = new ArrayList<>();
        Deque<TreeNode<T>> stack = new ArrayDeque<>();
        TreeNode<T> cur = root;
        while (cur != null || !stack.isEmpty()) {
            while (cur != null) {
                stack.push(cur);
                cur = cur.left;
            }
            cur = stack.pop();
            res.add(cur.val);
            cur = cur.right;
        }
        return res;
    }

    public static <T> List<T> postorderIter(TreeNode<T> root) {
        List<T> res = new ArrayList<>();
        if (root == null) return res;
        Deque<TreeNode<T>> stack = new ArrayDeque<>();
        TreeNode<T> prev = null;
        stack.push(root);
        while (!stack.isEmpty()) {
            TreeNode<T> cur = stack.peek();
            if ((cur.left == null && cur.right == null) ||
                    (prev != null && (prev == cur.left || prev == cur.right))) {
                res.add(cur.val);
                stack.pop();
                prev = cur;
            } else {
                if (cur.right != null) stack.push(cur.right);
                if (cur.left != null) stack.push(cur.left);
            }
        }
        return res;
    }

    // 层序遍历
    public static <T> List<List<T>> levelOrder(TreeNode<T> root) {
        List<List<T>> res = new ArrayList<>();
        if (root == null) return res;
        Queue<TreeNode<T>> queue = new LinkedList<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            int size = queue.size();
            List<T> level = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                TreeNode<T> node = queue.poll();
                level.add(node.val);
                if (node.left != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
            }
            res.add(level);
        }
        return res;
    }

    // 最大深度
    public static <T> int maxDepth(TreeNode<T> root) {
        if (root == null) return 0;
        return 1 + Math.max(maxDepth(root.left), maxDepth(root.right));
    }

    // 节点数量
    public static <T> int countNodes(TreeNode<T> root) {
        if (root == null) return 0;
        return 1 + countNodes(root.left) + countNodes(root.right);
    }

    // 是否平衡（AVL 定义：任意节点左右子树高度差不超过 1）
    public static <T> boolean isBalanced(TreeNode<T> root) {
        return heightOrUnbalanced(root) >= 0;
    }

    private static <T> int heightOrUnbalanced(TreeNode<T> node) {
        if (node == null) return 0;
        int lh = heightOrUnbalanced(node.left);
        if (lh < 0) return -1;
        int rh = heightOrUnbalanced(node.right);
        if (rh < 0) return -1;
        if (Math.abs(lh - rh) > 1) return -1;
        return 1 + Math.max(lh, rh);
    }
}