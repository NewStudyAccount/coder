package com.example.demo;

import java.util.*;

/**
 * 二叉搜索树（BST）实现：支持插入、查找、删除、最小/最大值、floor/ceil、kthSmallest、遍历与验证。
 * 不允许重复元素（插入已存在值将忽略）。泛型要求可比较。
 */
public class BinarySearchTree<T extends Comparable<T>> {

    static class Node<T> {
        T val;
        Node<T> left, right;
        Node(T v) { this.val = v; }
    }

    private Node<T> root;
    private int size;

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }

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
        // cmp == 0 忽略重复
        return node;
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

    // floor: <= x 的最大值
    public T floor(T x) {
        Node<T> cur = root;
        T res = null;
        while (cur != null) {
            int cmp = x.compareTo(cur.val);
            if (cmp == 0) return cur.val;
            if (cmp < 0) {
                cur = cur.left;
            } else {
                res = cur.val;
                cur = cur.right;
            }
        }
        return res;
    }

    // ceil: >= x 的最小值
    public T ceil(T x) {
        Node<T> cur = root;
        T res = null;
        while (cur != null) {
            int cmp = x.compareTo(cur.val);
            if (cmp == 0) return cur.val;
            if (cmp > 0) {
                cur = cur.right;
            } else {
                res = cur.val;
                cur = cur.left;
            }
        }
        return res;
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
            if (node.left == null && node.right == null) {
                size--;
                return null;
            } else if (node.left == null) {
                size--;
                return node.right;
            } else if (node.right == null) {
                size--;
                return node.left;
            } else {
                // 两个子节点：用后继替换（也可用前驱）
                Node<T> succ = node.right;
                while (succ.left != null) succ = succ.left;
                node.val = succ.val;
                node.right = delete(node.right, succ.val);
            }
        }
        return node;
    }

    // 中序遍历（有序）
    public List<T> inorder() {
        List<T> res = new ArrayList<>();
        Deque<Node<T>> stack = new ArrayDeque<>();
        Node<T> cur = root;
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

    // 验证 BST
    public boolean isValidBST() {
        return isValidBST(root, null, null);
    }

    private boolean isValidBST(Node<T> node, T min, T max) {
        if (node == null) return true;
        if (min != null && node.val.compareTo(min) <= 0) return false;
        if (max != null && node.val.compareTo(max) >= 0) return false;
        return isValidBST(node.left, min, node.val) && isValidBST(node.right, node.val, max);
    }

    // 第 k 小（1-based），若 k 越界返回 null
    public T kthSmallest(int k) {
        if (k <= 0 || k > size) return null;
        Deque<Node<T>> stack = new ArrayDeque<>();
        Node<T> cur = root;
        int count = 0;
        while (cur != null || !stack.isEmpty()) {
            while (cur != null) {
                stack.push(cur);
                cur = cur.left;
            }
            cur = stack.pop();
            count++;
            if (count == k) return cur.val;
            cur = cur.right;
        }
        return null;
    }
}