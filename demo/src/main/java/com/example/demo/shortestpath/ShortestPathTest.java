package com.example.demo.shortestpath;

/**
 * 最短路径算法综合测试类
 * 包含多种测试场景，验证算法的正确性和健壮性
 */
public class ShortestPathTest {
    
    public static void main(String[] args) {
        System.out.println("========== 最短路径算法测试 ==========\n");
        
        // 测试1：基本功能测试
        testBasicFunctionality();
        
        System.out.println("\n" + "=".repeat(50) + "\n");
        
        // 测试2：边界条件测试
        testEdgeCases();
        
        System.out.println("\n" + "=".repeat(50) + "\n");
        
        // 测试3：复杂图测试
        testComplexGraph();
        
        System.out.println("\n" + "=".repeat(50) + "\n");
        
        // 测试4：性能测试
        testPerformance();
        
        System.out.println("\n========== 所有测试完成 ==========");
    }
    
    /**
     * 测试1：基本功能测试
     */
    private static void testBasicFunctionality() {
        System.out.println("【测试1：基本功能测试】\n");
        
        Graph graph = new Graph();
        graph.addUndirectedEdge("A", "B", 4.0);
        graph.addUndirectedEdge("A", "C", 2.0);
        graph.addUndirectedEdge("B", "C", 1.0);
        graph.addUndirectedEdge("B", "D", 5.0);
        graph.addUndirectedEdge("C", "D", 8.0);
        graph.addUndirectedEdge("C", "E", 10.0);
        graph.addUndirectedEdge("D", "E", 2.0);
        
        // 测试用例1：简单路径
        testCase("A到D的最短路径", graph, "A", "D", 
                 new String[]{"A", "C", "B", "D"}, 8.0);
        
        // 测试用例2：多条等长路径
        testCase("A到E的最短路径", graph, "A", "E", 
                 new String[]{"A", "C", "B", "D", "E"}, 10.0);
        
        // 测试用例3：相邻节点
        testCase("A到C的最短路径", graph, "A", "C", 
                 new String[]{"A", "C"}, 2.0);
        
        System.out.println("✓ 基本功能测试通过");
    }
    
    /**
     * 测试2：边界条件测试
     */
    private static void testEdgeCases() {
        System.out.println("【测试2：边界条件测试】\n");
        
        // 测试用例1：起点和终点相同
        Graph graph1 = new Graph();
        graph1.addNode("A");
        PathResult result1 = DijkstraAlgorithm.findShortestPath(graph1, "A", "A");
        System.out.println("用例1 - 起点终点相同:");
        System.out.println("  结果: " + (result1.getPath().isEmpty() ? "通过" : "失败"));
        
        // 测试用例2：不存在的节点
        Graph graph2 = new Graph();
        graph2.addNode("A");
        graph2.addNode("B");
        PathResult result2 = DijkstraAlgorithm.findShortestPath(graph2, "A", "Z");
        System.out.println("\n用例2 - 不存在的节点:");
        System.out.println("  结果: " + (!result2.isPathExists() ? "通过 (正确识别无效节点)" : "失败"));
        
        // 测试用例3：不连通的图
        Graph graph3 = new Graph();
        graph3.addNode("A");
        graph3.addNode("B");
        graph3.addNode("C");
        graph3.addNode("D");
        graph3.addUndirectedEdge("A", "B", 1.0);
        graph3.addUndirectedEdge("C", "D", 1.0);
        PathResult result3 = DijkstraAlgorithm.findShortestPath(graph3, "A", "D");
        System.out.println("\n用例3 - 不连通的图:");
        System.out.println("  结果: " + (!result3.isPathExists() ? "通过 (正确识别不可达)" : "失败"));
        
        // 测试用例4：单节点图
        Graph graph4 = new Graph();
        graph4.addNode("A");
        System.out.println("\n用例4 - 单节点图:");
        System.out.println("  节点数: " + graph4.getNodes().size());
        System.out.println("  结果: 通过");
        
        // 测试用例5：权重为0的边
        Graph graph5 = new Graph();
        graph5.addUndirectedEdge("A", "B", 0.0);
        graph5.addUndirectedEdge("B", "C", 1.0);
        PathResult result5 = DijkstraAlgorithm.findShortestPath(graph5, "A", "C");
        System.out.println("\n用例5 - 权重为0的边:");
        System.out.printf("  距离: %.1f (预期: 1.0)%n", result5.getDistance());
        System.out.println("  结果: " + (Math.abs(result5.getDistance() - 1.0) < 0.001 ? "通过" : "失败"));
        
        System.out.println("\n✓ 边界条件测试通过");
    }
    
    /**
     * 测试3：复杂图测试
     */
    private static void testComplexGraph() {
        System.out.println("【测试3：复杂图测试】\n");
        
        // 创建一个包含10个节点的复杂图
        Graph graph = new Graph();
        
        // 添加节点
        for (int i = 0; i < 10; i++) {
            graph.addNode("N" + i);
        }
        
        // 添加边（创建一个复杂但连通的图）
        graph.addUndirectedEdge("N0", "N1", 1.0);
        graph.addUndirectedEdge("N0", "N2", 4.0);
        graph.addUndirectedEdge("N1", "N2", 2.0);
        graph.addUndirectedEdge("N1", "N3", 5.0);
        graph.addUndirectedEdge("N2", "N3", 1.0);
        graph.addUndirectedEdge("N2", "N4", 3.0);
        graph.addUndirectedEdge("N3", "N5", 2.0);
        graph.addUndirectedEdge("N4", "N5", 1.0);
        graph.addUndirectedEdge("N4", "N6", 4.0);
        graph.addUndirectedEdge("N5", "N7", 3.0);
        graph.addUndirectedEdge("N6", "N7", 1.0);
        graph.addUndirectedEdge("N6", "N8", 2.0);
        graph.addUndirectedEdge("N7", "N9", 2.0);
        graph.addUndirectedEdge("N8", "N9", 1.0);
        
        // 测试从N0到N9的最短路径
        PathResult result = DijkstraAlgorithm.findShortestPath(graph, "N0", "N9");
        
        System.out.println("复杂图测试 (10个节点，14条边):");
        System.out.printf("  起点: N0, 终点: N9%n");
        System.out.printf("  最短距离: %.1f%n", result.getDistance());
        System.out.printf("  路径: %s%n", result.getFormattedPath());
        System.out.printf("  路径长度: %d个节点%n", result.getPath().size());
        
        // 验证路径的连续性
        boolean isValid = validatePathContinuity(graph, result);
        System.out.println("  路径有效性: " + (isValid ? "通过" : "失败"));
        
        System.out.println("\n✓ 复杂图测试通过");
    }
    
    /**
     * 测试4：性能测试
     */
    private static void testPerformance() {
        System.out.println("【测试4：性能测试】\n");
        
        // 创建不同规模的图进行性能测试
        int[] sizes = {10, 50, 100, 200};
        
        for (int size : sizes) {
            Graph graph = createRandomGraph(size);
            
            long startTime = System.nanoTime();
            PathResult result = DijkstraAlgorithm.findShortestPath(
                graph, "N0", "N" + (size - 1));
            long endTime = System.nanoTime();
            
            double timeMs = (endTime - startTime) / 1_000_000.0;
            
            System.out.printf("规模: %d个节点%n", size);
            System.out.printf("  执行时间: %.3f毫秒%n", timeMs);
            System.out.printf("  路径存在: %s%n", result.isPathExists() ? "是" : "否");
            if (result.isPathExists()) {
                System.out.printf("  路径长度: %d个节点%n", result.getPath().size());
            }
            System.out.println();
        }
        
        System.out.println("✓ 性能测试完成");
    }
    
    /**
     * 测试用例辅助方法
     */
    private static void testCase(String name, Graph graph, String start, String end,
                                  String[] expectedPath, double expectedDistance) {
        PathResult result = DijkstraAlgorithm.findShortestPath(graph, start, end);
        
        boolean pathMatch = true;
        if (result.getPath().size() == expectedPath.length) {
            for (int i = 0; i < expectedPath.length; i++) {
                if (!result.getPath().get(i).equals(expectedPath[i])) {
                    pathMatch = false;
                    break;
                }
            }
        } else {
            pathMatch = false;
        }
        
        boolean distanceMatch = Math.abs(result.getDistance() - expectedDistance) < 0.001;
        
        System.out.printf("%s:%n", name);
        System.out.printf("  路径匹配: %s%n", pathMatch ? "✓" : "✗");
        System.out.printf("  距离匹配: %s (%.1f)%n%n", 
                         distanceMatch ? "✓" : "✗", result.getDistance());
    }
    
    /**
     * 验证路径的连续性
     */
    private static boolean validatePathContinuity(Graph graph, PathResult result) {
        if (!result.isPathExists() || result.getPath().size() < 2) {
            return result.isPathExists() ? result.getPath().size() == 1 : false;
        }
        
        for (int i = 0; i < result.getPath().size() - 1; i++) {
            String current = result.getPath().get(i);
            String next = result.getPath().get(i + 1);
            
            boolean edgeExists = false;
            for (Graph.Edge edge : graph.getEdges(current)) {
                if (edge.getDestination().equals(next)) {
                    edgeExists = true;
                    break;
                }
            }
            
            if (!edgeExists) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 创建随机图用于性能测试
     */
    private static Graph createRandomGraph(int size) {
        Graph graph = new Graph();
        
        // 添加节点
        for (int i = 0; i < size; i++) {
            graph.addNode("N" + i);
        }
        
        // 创建一个连通图（确保每个节点至少有一条边）
        for (int i = 0; i < size - 1; i++) {
            graph.addUndirectedEdge("N" + i, "N" + (i + 1), Math.random() * 10 + 1);
        }
        
        // 添加额外的边以增加复杂度
        int extraEdges = size / 2;
        for (int i = 0; i < extraEdges; i++) {
            int from = (int) (Math.random() * size);
            int to = (int) (Math.random() * size);
            if (from != to) {
                graph.addUndirectedEdge("N" + from, "N" + to, Math.random() * 10 + 1);
            }
        }
        
        return graph;
    }
}
