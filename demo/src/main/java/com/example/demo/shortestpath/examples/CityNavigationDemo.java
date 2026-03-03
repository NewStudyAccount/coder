package com.example.demo.shortestpath.examples;

import com.example.demo.shortestpath.DijkstraAlgorithm;
import com.example.demo.shortestpath.Graph;
import com.example.demo.shortestpath.PathResult;

import java.util.Map;

/**
 * 城市导航系统演示
 * 实际应用场景：地图导航、路线规划
 * 
 * 场景描述：
 * 在一个城市中有多个地点，地点之间有道路连接，每条道路有不同的距离（公里）。
 * 我们需要找到从一个地点到另一个地点的最短路径。
 */
public class CityNavigationDemo {
    
    public static void main(String[] args) {
        System.out.println("========== 城市导航系统演示 ==========\n");
        
        // 创建城市地图
        Graph cityMap = buildCityMap();
        
        // 演示1：找到从家到公司的最短路径
        demonstrateSinglePath(cityMap);
        
        System.out.println("\n" + "=".repeat(50) + "\n");
        
        // 演示2：从家出发到所有其他地点的最短路径
        demonstrateAllPaths(cityMap);
        
        System.out.println("\n" + "=".repeat(50) + "\n");
        
        // 演示3：多个起点和终点的路径规划
        demonstrateMultipleRoutes(cityMap);
    }
    
    /**
     * 构建城市地图
     * 
     * 城市地图包含以下地点：
     * - 家(Home)
     * - 公司(Office)
     * - 超市(Supermarket)
     * - 健身房(Gym)
     * - 公园(Park)
     * - 餐厅(Restaurant)
     * - 医院(Hospital)
     */
    private static Graph buildCityMap() {
        Graph graph = new Graph();
        
        // 添加地点（节点）
        graph.addNode("家");
        graph.addNode("公司");
        graph.addNode("超市");
        graph.addNode("健身房");
        graph.addNode("公园");
        graph.addNode("餐厅");
        graph.addNode("医院");
        
        // 添加道路（无向边）及距离（公里）
        // 从家出发的路线
        graph.addUndirectedEdge("家", "超市", 2.5);
        graph.addUndirectedEdge("家", "公园", 3.0);
        graph.addUndirectedEdge("家", "公司", 8.0);
        
        // 超市相关路线
        graph.addUndirectedEdge("超市", "健身房", 1.5);
        graph.addUndirectedEdge("超市", "餐厅", 2.0);
        
        // 公园相关路线
        graph.addUndirectedEdge("公园", "健身房", 4.0);
        graph.addUndirectedEdge("公园", "医院", 3.5);
        
        // 健身房相关路线
        graph.addUndirectedEdge("健身房", "公司", 2.0);
        graph.addUndirectedEdge("健身房", "餐厅", 1.0);
        
        // 餐厅相关路线
        graph.addUndirectedEdge("餐厅", "公司", 3.5);
        graph.addUndirectedEdge("餐厅", "医院", 2.5);
        
        // 公司相关路线
        graph.addUndirectedEdge("公司", "医院", 4.0);
        
        return graph;
    }
    
    /**
     * 演示单条路径查询
     */
    private static void demonstrateSinglePath(Graph cityMap) {
        System.out.println("【场景1：上班路线规划】");
        System.out.println("需求：找到从家到公司的最短路径\n");
        
        PathResult result = DijkstraAlgorithm.findShortestPath(cityMap, "家", "公司");
        
        System.out.println(result);
        System.out.println("\n分析：");
        System.out.println("虽然有直达路线（8.0公里），但通过超市和健身房中转");
        System.out.println("只需要6.0公里，节省了2.0公里的路程。");
    }
    
    /**
     * 演示从一个起点到所有其他地点的路径
     */
    private static void demonstrateAllPaths(Graph cityMap) {
        System.out.println("【场景2：从家出发的所有目的地路线】");
        System.out.println("需求：规划从家到城市中所有其他地点的最短路径\n");
        
        Map<String, PathResult> allPaths = DijkstraAlgorithm.findAllShortestPaths(cityMap, "家");
        
        // 按距离排序并输出
        allPaths.entrySet().stream()
            .sorted(Map.Entry.comparingByValue(
                (p1, p2) -> Double.compare(p1.getDistance(), p2.getDistance())
            ))
            .forEach(entry -> {
                PathResult result = entry.getValue();
                System.out.printf("到 %s: %.1f公里 | 路线: %s%n",
                    entry.getKey(),
                    result.getDistance(),
                    result.getFormattedPath()
                );
            });
        
        System.out.println("\n分析：");
        System.out.println("通过一次计算，可以得到从家到所有地点的最优路线，");
        System.out.println("这对于外卖配送、快递规划等场景非常有用。");
    }
    
    /**
     * 演示多个不同的路线规划
     */
    private static void demonstrateMultipleRoutes(Graph cityMap) {
        System.out.println("【场景3：一天的行程规划】");
        System.out.println("需求：规划一天中的多个行程\n");
        
        String[][] routes = {
            {"家", "超市", "购物"},
            {"超市", "健身房", "锻炼"},
            {"健身房", "餐厅", "午餐"},
            {"餐厅", "公司", "上班"},
            {"公司", "医院", "体检"},
            {"医院", "家", "回家"}
        };
        
        double totalDistance = 0;
        
        System.out.println("今日行程：\n");
        for (int i = 0; i < routes.length; i++) {
            String from = routes[i][0];
            String to = routes[i][1];
            String activity = routes[i][2];
            
            PathResult result = DijkstraAlgorithm.findShortestPath(cityMap, from, to);
            
            System.out.printf("%d. %s → %s (%s)%n", i + 1, from, to, activity);
            System.out.printf("   距离: %.1f公里 | 路线: %s%n%n",
                result.getDistance(),
                result.getFormattedPath()
            );
            
            totalDistance += result.getDistance();
        }
        
        System.out.printf("今日总行程：%.1f公里%n", totalDistance);
        System.out.println("\n分析：");
        System.out.println("通过最短路径规划，可以优化一天的出行路线，");
        System.out.println("节省时间和燃油成本。这在共享出行、物流配送等领域应用广泛。");
    }
}
