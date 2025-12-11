package com.example.demo;

import java.util.*;

/**
 * AVL 平衡二叉搜索树：在 BST 基础上维护高度并进行旋转，保证任意节点平衡因子（左高-右高）在 [-1,1]。
 * 支持插入、删除、搜索、最小/最大、遍历与验证平衡。
 * 不允许重复元素（插入已存在值将忽略）。泛型要求可比较。
 */
public class AvlTree<T extends Comparable<T>> {

    static class Node<T> {
        T val;
        Node<T> left, right;
        int height;
        Node(T v) { this.val = v; this.height = 1; }
    }

    private Node<T> root;
    private int size;

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }

    // 工具：高度与平衡因子
    private int h(Node<T> n) { return n == null ? 0 : n.height; }
    private int bf(Node<T> n) { return n == null ? 0 : h(n.left) - h(n.right); }
    private void update(Node<T> n) {
        if (n != null) n.height = 1 + Math.max(h(n.left), h(n.right));
    }

    // 右旋（LL）
    private Node<T> rotateRight(Node<T> y) {
        Node<T> x = y.left;
        Node<T> T2 = x.right;
        x.right = y;
        y.left = T2;
        update(y);
        update(x);
        return x;
    }

    // 左旋（RR）
    private Node<T> rotateLeft(Node<T> x) {
        Node<T> y = x.right;
        Node<T> T2 = y.left;
        y.left = x;
        x.right = T2;
        update(x);
        update(y);
        return y;
    }

    // 重新平衡某节点
    private Node<T> rebalance(Node<T> node) {
        update(node);
        int balance = bf(node);

        // LL
        if (balance > 1 && bf(node.left) >= 0) {
            return rotateRight(node);
        }
        // LR
        if (balance > 1 && bf(node.left) < 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }
        // RR
        if (balance < -1 && bf(node.right) <= 0) {
            return rotateLeft(node);
        }
        // RL
        if (balance < -1 && bf(node.right) > 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }
        return node;
    }

    // 插入（忽略重复）
    public void insert(T val) {
        root = insert(root, val);
    }

    private Node<T> insert(Node<T> node, T val) {
        if (node == null) {
            size++;
            return new Node<>(val);
        }
        int cmp = val.compareTo(node.val);
        if (cmp < 0) node.left = insert(node.left, val);
        else if (cmp > 0) node.right = insert(node.right, val);
        // else 重复忽略
        return rebalance(node);
    }

    // 搜索
    public boolean contains(T val) {
        Node<T> cur = root;
        while (cur != null) {
            int cmp = val.compareTo(cur.val);
            if (cmp == 0) return true;
            cur = (cmp < 0) ? cur.left : cur.right;
        }
        return false;
    }

    public T min() {
        if (root == null) return null;
        Node<T> cur = root;
        while (cur.left != null) cur = cur.left;
        return cur.val;
    }

    public T max() {
        if (root == null) return null;
        Node<T> cur = root;
        while (cur.right != null) cur = cur.right;
        return cur.val;
    }

    // 删除
    public void delete(T val) {
        root = delete(root, val);
    }

    private Node<T> delete(Node<T> node, T val) {
        if (node == null) return null;
        int cmp = val.compareTo(node.val);
        if (cmp < 0) {
            node.left = delete(node.left, val);
        } else if (cmp > 0) {
            node.right = delete(node.right, val);
        } else {
            // 命中
            if (node.left == null || node.right == null) {
                Node<T> child = (node.left != null) ? node.left : node.right;
                if (child == null) {
                    // 叶子
                    node = null;
                } else {
                    node = child;
                }
                size--;
            } else {
                // 两子：取右子树最小（后继）
                Node<T> succ = node.right;
                while (succ.left != null) succ = succ.left;
                node.val = succ.val;
                node.right = delete(node.right, succ.val);
            }
        }
        if (node == null) return null;
        return rebalance(node);
    }

    // 遍历
    public List<T> inorder() {
        List<T> res = new ArrayList<>();
        Deque<Node<T>> stack = new ArrayDeque<>();
        Node<T> cur = root;
        while (cur != null || !stack.isEmpty()) {
            while (cur != null) { stack.push(cur); cur = cur.left; }
            cur = stack.pop();
            res.add(cur.val);
            cur = cur.right;
        }
        return res;
    }

    public List<T> preorder() {
        List<T> res = new ArrayList<>();
        if (root == null) return res;
        Deque<Node<T>> stack = new ArrayDeque<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            Node<T> n = stack.pop();
            res.add(n.val);
            if (n.right != null) stack.push(n.right);
            if (n.left != null) stack.push(n.left);
        }
        return res;
    }

    public List<T> postorder() {
        List<T> res = new ArrayList<>();
        if (root == null) return res;
        Deque<Node<T>> stack = new ArrayDeque<>();
        Node<T> prev = null;
        stack.push(root);
        while (!stack.isEmpty()) {
            Node<T> cur = stack.peek();
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

    // 验证平衡性（每个节点平衡因子范围 [-1,1]）
    public boolean isBalanced() {
        return checkBalance(root) >= 0;
    }

    private int checkBalance(Node<T> node) {
        if (node == null) return 0;
        int lh = checkBalance(node.left);
        if (lh < 0) return -1;
        int rh = checkBalance(node.right);
        if (rh < 0) return -1;
        if (Math.abs(lh - rh) > 1) return -1;
        return 1 + Math.max(lh, rh);
    }
}