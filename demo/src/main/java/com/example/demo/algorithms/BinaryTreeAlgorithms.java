package com.example.demo.algorithms;

import java.util.*;

/**
 * 二叉树常用算法合集（新文件）。
 * - 定义最小泛型二叉树节点 TreeNode，便于独立使用。
 * - 提供遍历（前/中/后序，递归与迭代）、层序（含分层/自底向上/锯齿形）、深度/节点计数、
 *   验证二叉搜索树、最近公共祖先、最大路径和、翻转二叉树、对称性判断、序列化/反序列化、构建树等。
 * - 方法均为静态、无状态。
 */
public final class BinaryTreeAlgorithms {

    private BinaryTreeAlgorithms() {
    }

    // ============ 基本数据结构 ============
    public static class TreeNode<T> {
        public T val;
        public TreeNode<T> left;
        public TreeNode<T> right;
        public TreeNode(T val) { this.val = val; }
        public TreeNode(T val, TreeNode<T> left, TreeNode<T> right) {
            this.val = val; this.left = left; this.right = right;
        }
    }

    // ============ 遍历：前/中/后序（递归） ============
    public static <T> List<T> preOrderRecursive(TreeNode<T> root) {
        List<T> res = new ArrayList<>();
        preOrderDfs(root, res);
        return res;
    }
    private static <T> void preOrderDfs(TreeNode<T> node, List<T> res) {
        if (node == null) return;
        res.add(node.val);
        preOrderDfs(node.left, res);
        preOrderDfs(node.right, res);
    }

    public static <T> List<T> inOrderRecursive(TreeNode<T> root) {
        List<T> res = new ArrayList<>();
        inOrderDfs(root, res);
        return res;
    }
    private static <T> void inOrderDfs(TreeNode<T> node, List<T> res) {
        if (node == null) return;
        inOrderDfs(node.left, res);
        res.add(node.val);
        inOrderDfs(node.right, res);
    }

    public static <T> List<T> postOrderRecursive(TreeNode<T> root) {
        List<T> res = new ArrayList<>();
        postOrderDfs(root, res);
        return res;
    }
    private static <T> void postOrderDfs(TreeNode<T> node, List<T> res) {
        if (node == null) return;
        postOrderDfs(node.left, res);
        postOrderDfs(node.right, res);
        res.add(node.val);
    }

    // ============ 遍历：前/中/后序（迭代） ============
    public static <T> List<T> preOrderIterative(TreeNode<T> root) {
        List<T> res = new ArrayList<>();
        if (root == null) return res;
        Deque<TreeNode<T>> stack = new ArrayDeque<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            TreeNode<T> cur = stack.pop();
            res.add(cur.val);
            if (cur.right != null) stack.push(cur.right);
            if (cur.left != null) stack.push(cur.left);
        }
        return res;
    }

    public static <T> List<T> inOrderIterative(TreeNode<T> root) {
        List<T> res = new ArrayList<>();
        Deque<TreeNode<T>> stack = new ArrayDeque<>();
        TreeNode<T> cur = root;
        while (cur != null || !stack.isEmpty()) {
            while (cur != null) { stack.push(cur); cur = cur.left; }
            cur = stack.pop();
            res.add(cur.val);
            cur = cur.right;
        }
        return res;
    }

    public static <T> List<T> postOrderIterative(TreeNode<T> root) {
        List<T> res = new ArrayList<>();
        Deque<TreeNode<T>> stack = new ArrayDeque<>();
        TreeNode<T> cur = root, last = null;
        while (cur != null || !stack.isEmpty()) {
            if (cur != null) { stack.push(cur); cur = cur.left; }
            else {
                TreeNode<T> peek = stack.peek();
                if (peek.right != null && last != peek.right) {
                    cur = peek.right;
                } else {
                    res.add(peek.val);
                    last = stack.pop();
                }
            }
        }
        return res;
    }

    // ============ 层序遍历（BFS）及变体 ============
    public static <T> List<T> levelOrderFlat(TreeNode<T> root) {
        List<T> res = new ArrayList<>();
        if (root == null) return res;
        Deque<TreeNode<T>> q = new ArrayDeque<>();
        q.offer(root);
        while (!q.isEmpty()) {
            TreeNode<T> cur = q.poll();
            res.add(cur.val);
            if (cur.left != null) q.offer(cur.left);
            if (cur.right != null) q.offer(cur.right);
        }
        return res;
    }

    public static <T> List<List<T>> levelOrder(TreeNode<T> root) {
        List<List<T>> res = new ArrayList<>();
        if (root == null) return res;
        Deque<TreeNode<T>> q = new ArrayDeque<>();
        q.offer(root);
        while (!q.isEmpty()) {
            int size = q.size();
            List<T> layer = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                TreeNode<T> cur = q.poll();
                layer.add(cur.val);
                if (cur.left != null) q.offer(cur.left);
                if (cur.right != null) q.offer(cur.right);
            }
            res.add(layer);
        }
        return res;
    }

    public static <T> List<List<T>> levelOrderBottom(TreeNode<T> root) {
        LinkedList<List<T>> res = new LinkedList<>();
        if (root == null) return res;
        Deque<TreeNode<T>> q = new ArrayDeque<>();
        q.offer(root);
        while (!q.isEmpty()) {
            int size = q.size();
            List<T> layer = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                TreeNode<T> cur = q.poll();
                layer.add(cur.val);
                if (cur.left != null) q.offer(cur.left);
                if (cur.right != null) q.offer(cur.right);
            }
            res.addFirst(layer);
        }
        return res;
    }

    public static <T> List<List<T>> levelOrderZigzag(TreeNode<T> root) {
        List<List<T>> res = new ArrayList<>();
        if (root == null) return res;
        Deque<TreeNode<T>> q = new ArrayDeque<>();
        q.offer(root);
        boolean leftToRight = true;
        while (!q.isEmpty()) {
            int size = q.size();
            LinkedList<T> layer = new LinkedList<>();
            for (int i = 0; i < size; i++) {
                TreeNode<T> cur = q.poll();
                if (leftToRight) layer.addLast(cur.val);
                else layer.addFirst(cur.val);
                if (cur.left != null) q.offer(cur.left);
                if (cur.right != null) q.offer(cur.right);
            }
            res.add(new ArrayList<>(layer));
            leftToRight = !leftToRight;
        }
        return res;
    }

    // ============ 基本性质：深度/节点数/平衡性/对称性 ============
    public static <T> int maxDepth(TreeNode<T> root) {
        if (root == null) return 0;
        return 1 + Math.max(maxDepth(root.left), maxDepth(root.right));
    }

    public static <T> int minDepth(TreeNode<T> root) {
        if (root == null) return 0;
        if (root.left == null) return 1 + minDepth(root.right);
        if (root.right == null) return 1 + minDepth(root.left);
        return 1 + Math.min(minDepth(root.left), minDepth(root.right));
    }

    public static <T> int countNodes(TreeNode<T> root) {
        if (root == null) return 0;
        return 1 + countNodes(root.left) + countNodes(root.right);
    }

    public static <T> boolean isBalanced(TreeNode<T> root) {
        return heightOrUnbalanced(root) != -1;
    }
    private static <T> int heightOrUnbalanced(TreeNode<T> node) {
        if (node == null) return 0;
        int lh = heightOrUnbalanced(node.left);
        if (lh == -1) return -1;
        int rh = heightOrUnbalanced(node.right);
        if (rh == -1) return -1;
        if (Math.abs(lh - rh) > 1) return -1;
        return 1 + Math.max(lh, rh);
    }

    public static <T> boolean isSymmetric(TreeNode<T> root) {
        return root == null || isMirror(root.left, root.right);
    }
    private static <T> boolean isMirror(TreeNode<T> a, TreeNode<T> b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        if (!Objects.equals(a.val, b.val)) return false;
        return isMirror(a.left, b.right) && isMirror(a.right, b.left);
    }

    // ============ 二叉搜索树（BST）相关 ============
    public static boolean isValidBST(TreeNode<Integer> root) {
        return isValidBST(root, null, null);
    }
    private static boolean isValidBST(TreeNode<Integer> node, Integer low, Integer high) {
        if (node == null) return true;
        Integer v = node.val;
        if (low != null && v <= low) return false;
        if (high != null && v >= high) return false;
        return isValidBST(node.left, low, v) && isValidBST(node.right, v, high);
    }

    public static TreeNode<Integer> bstInsert(TreeNode<Integer> root, int val) {
        if (root == null) return new TreeNode<>(val);
        if (val < root.val) root.left = bstInsert(root.left, val);
        else if (val > root.val) root.right = bstInsert(root.right, val);
        return root;
    }

    public static TreeNode<Integer> bstSearch(TreeNode<Integer> root, int val) {
        TreeNode<Integer> cur = root;
        while (cur != null) {
            if (val == cur.val) return cur;
            cur = (val < cur.val) ? cur.left : cur.right;
        }
        return null;
    }

    public static TreeNode<Integer> bstDelete(TreeNode<Integer> root, int key) {
        if (root == null) return null;
        if (key < root.val) root.left = bstDelete(root.left, key);
        else if (key > root.val) root.right = bstDelete(root.right, key);
        else {
            if (root.left == null) return root.right;
            if (root.right == null) return root.left;
            TreeNode<Integer> succ = root.right;
            while (succ.left != null) succ = succ.left;
            root.val = succ.val;
            root.right = bstDelete(root.right, succ.val);
        }
        return root;
    }

    // ============ 其他常见题型 ============
    public static <T> TreeNode<T> invertTree(TreeNode<T> root) {
        if (root == null) return null;
        TreeNode<T> left = invertTree(root.left);
        TreeNode<T> right = invertTree(root.right);
        root.left = right;
        root.right = left;
        return root;
    }

    public static int diameterOfBinaryTree(TreeNode<Integer> root) {
        int[] max = new int[1];
        diameterDfs(root, max);
        return max[0];
    }
    private static int diameterDfs(TreeNode<Integer> node, int[] max) {
        if (node == null) return 0;
        int lh = diameterDfs(node.left, max);
        int rh = diameterDfs(node.right, max);
        max[0] = Math.max(max[0], lh + rh);
        return 1 + Math.max(lh, rh);
    }

    public static int maxPathSum(TreeNode<Integer> root) {
        int[] ans = new int[]{Integer.MIN_VALUE};
        maxPathSumDfs(root, ans);
        return ans[0];
    }
    private static int maxPathSumDfs(TreeNode<Integer> node, int[] ans) {
        if (node == null) return 0;
        int l = Math.max(0, maxPathSumDfs(node.left, ans));
        int r = Math.max(0, maxPathSumDfs(node.right, ans));
        ans[0] = Math.max(ans[0], l + r + node.val);
        return node.val + Math.max(l, r);
    }

    public static <T> boolean hasPath(TreeNode<T> root, List<T> path) {
        return hasPathDfs(root, path, 0);
    }
    private static <T> boolean hasPathDfs(TreeNode<T> node, List<T> path, int i) {
        if (node == null) return false;
        if (!Objects.equals(node.val, path.get(i))) return false;
        if (i == path.size() - 1) return true;
        return hasPathDfs(node.left, path, i + 1) || hasPathDfs(node.right, path, i + 1);
    }

    public static <T> TreeNode<T> lowestCommonAncestor(TreeNode<T> root, T p, T q) {
        if (root == null) return null;
        if (Objects.equals(root.val, p) || Objects.equals(root.val, q)) return root;
        TreeNode<T> left = lowestCommonAncestor(root.left, p, q);
        TreeNode<T> right = lowestCommonAncestor(root.right, p, q);
        if (left != null && right != null) return root;
        return left != null ? left : right;
    }

    // ============ 序列化 / 反序列化（层序） ============
    // 使用 'null' 作为空指针标记；序列格式：以逗号分隔的值，如 "1,2,3,null,null,4,null"
    public static String serializeLevelOrder(TreeNode<Integer> root) {
        if (root == null) return "";
        StringBuilder sb = new StringBuilder();
        Deque<TreeNode<Integer>> q = new ArrayDeque<>();
        q.offer(root);
        while (!q.isEmpty()) {
            TreeNode<Integer> node = q.poll();
            if (node == null) {
                sb.append("null").append(",");
                continue;
            }
            sb.append(node.val).append(",");
            q.offer(node.left);
            q.offer(node.right);
        }
        // 去尾部冗余 null
        int i = sb.length() - 1;
        while (i >= 0 && (sb.charAt(i) == ',' || sb.charAt(i) == 'l')) {
            // 寻找尾部的 ",null" 模式并裁剪
            if (i >= 4 && sb.substring(i - 4 + 1, i + 1).equals("null")) {
                i -= 5; // 跳过 ",null"
            } else {
                break;
            }
        }
        // 简化：直接去掉末尾逗号
        if (sb.length() > 0 && sb.charAt(sb.length() - 1) == ',') sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public static TreeNode<Integer> deserializeLevelOrder(String data) {
        if (data == null || data.isEmpty()) return null;
        String[] parts = data.split(",");
        if (parts.length == 0 || parts[0].equals("null")) return null;
        TreeNode<Integer> root = new TreeNode<>(Integer.parseInt(parts[0]));
        Deque<TreeNode<Integer>> q = new ArrayDeque<>();
        q.offer(root);
        int i = 1;
        while (!q.isEmpty() && i < parts.length) {
            TreeNode<Integer> cur = q.poll();
            // left
            if (i < parts.length && !parts[i].equals("null")) {
                cur.left = new TreeNode<>(Integer.parseInt(parts[i]));
                q.offer(cur.left);
            }
            i++;
            // right
            if (i < parts.length && !parts[i].equals("null")) {
                cur.right = new TreeNode<>(Integer.parseInt(parts[i]));
                q.offer(cur.right);
            }
            i++;
        }
        return root;
    }

    // ============ 构建树（辅助） ============
    // 从前序与中序构建（值唯一）
    public static TreeNode<Integer> buildTreeFromPreIn(int[] preorder, int[] inorder) {
        if (preorder == null || inorder == null || preorder.length != inorder.length) return null;
        Map<Integer, Integer> idx = new HashMap<>();
        for (int i = 0; i < inorder.length; i++) idx.put(inorder[i], i);
        return buildPreIn(preorder, 0, preorder.length - 1, inorder, 0, inorder.length - 1, idx);
    }
    private static TreeNode<Integer> buildPreIn(int[] pre, int ps, int pe, int[] in, int is, int ie, Map<Integer, Integer> idx) {
        if (ps > pe) return null;
        int rootVal = pre[ps];
        int k = idx.get(rootVal);
        int leftSize = k - is;
        TreeNode<Integer> root = new TreeNode<>(rootVal);
        root.left = buildPreIn(pre, ps + 1, ps + leftSize, in, is, k - 1, idx);
        root.right = buildPreIn(pre, ps + leftSize + 1, pe, in, k + 1, ie, idx);
        return root;
    }

    // 从中序与后序构建（值唯一）
    public static TreeNode<Integer> buildTreeFromInPost(int[] inorder, int[] postorder) {
        if (inorder == null || postorder == null || inorder.length != postorder.length) return null;
        Map<Integer, Integer> idx = new HashMap<>();
        for (int i = 0; i < inorder.length; i++) idx.put(inorder[i], i);
        return buildInPost(inorder, 0, inorder.length - 1, postorder, 0, postorder.length - 1, idx);
    }
    private static TreeNode<Integer> buildInPost(int[] in, int is, int ie, int[] post, int ps, int pe, Map<Integer, Integer> idx) {
        if (is > ie) return null;
        int rootVal = post[pe];
        int k = idx.get(rootVal);
        int leftSize = k - is;
        TreeNode<Integer> root = new TreeNode<>(rootVal);
        root.left = buildInPost(in, is, k - 1, post, ps, ps + leftSize - 1, idx);
        root.right = buildInPost(in, k + 1, ie, post, ps + leftSize, pe - 1, idx);
        return root;
    }

    // ============ 示例用法 ============
    public static void main(String[] args) {
        // 构建示例树：   1
        //              / \
        //             2   3
        //                / \
        //               4   5
        TreeNode<Integer> root = new TreeNode<>(1,
                new TreeNode<>(2),
                new TreeNode<>(3, new TreeNode<>(4), new TreeNode<>(5)));

        System.out.println("Pre(递归): " + preOrderRecursive(root));
        System.out.println("In (递归): " + inOrderRecursive(root));
        System.out.println("Post(递归): " + postOrderRecursive(root));
        System.out.println("Pre(迭代): " + preOrderIterative(root));
        System.out.println("In (迭代): " + inOrderIterative(root));
        System.out.println("Post(迭代): " + postOrderIterative(root));
        System.out.println("Level: " + levelOrder(root));
        System.out.println("Zigzag: " + levelOrderZigzag(root));
        System.out.println("MaxDepth: " + maxDepth(root));
        System.out.println("Diameter: " + diameterOfBinaryTree(root));
        System.out.println("IsBalanced: " + isBalanced(root));
        System.out.println("IsSymmetric: " + isSymmetric(root));
        System.out.println("Serialize: " + serializeLevelOrder(root));
    }
}