package com.example.demo.shortestpath.examples;

import com.example.demo.shortestpath.DijkstraAlgorithm;
import com.example.demo.shortestpath.Graph;
import com.example.demo.shortestpath.PathResult;

import java.util.Map;

/**
 * 网络路由系统演示
 * 实际应用场景：网络数据包路由、通信网络优化
 * 
 * 场景描述：
 * 在计算机网络中，数据包需要从源节点传输到目标节点。
 * 网络由多个路由器组成，路由器之间有链路连接。
 * 每条链路有不同的延迟（毫秒），我们需要找到延迟最小的路由路径。
 */
public class NetworkRoutingDemo {
    
    public static void main(String[] args) {
        System.out.println("========== 网络路由系统演示 ==========\n");
        
        // 创建网络拓扑
        Graph network = buildNetworkTopology();
        
        // 演示1：数据包路由
        demonstrateDataPacketRouting(network);
        
        System.out.println("\n" + "=".repeat(50) + "\n");
        
        // 演示2：网络中心节点分析
        demonstrateNetworkCentrality(network);
        
        System.out.println("\n" + "=".repeat(50) + "\n");
        
        // 演示3：网络故障容错
        demonstrateNetworkFailover(network);
    }
    
    /**
     * 构建网络拓扑
     * 
     * 网络节点：
     * - 北京服务器
     * - 上海服务器
     * - 广州服务器
     * - 深圳服务器
     * - 成都服务器
     * - 武汉服务器
     * - 西安服务器
     */
    private static Graph buildNetworkTopology() {
        Graph graph = new Graph();
        
        // 添加网络节点（路由器/服务器）
        graph.addNode("北京");
        graph.addNode("上海");
        graph.addNode("广州");
        graph.addNode("深圳");
        graph.addNode("成都");
        graph.addNode("武汉");
        graph.addNode("西安");
        
        // 添加网络链路及延迟（毫秒）
        // 北京的连接
        graph.addUndirectedEdge("北京", "上海", 15.0);
        graph.addUndirectedEdge("北京", "西安", 20.0);
        graph.addUndirectedEdge("北京", "武汉", 25.0);
        
        // 上海的连接
        graph.addUndirectedEdge("上海", "武汉", 18.0);
        graph.addUndirectedEdge("上海", "广州", 22.0);
        
        // 广州的连接
        graph.addUndirectedEdge("广州", "深圳", 5.0);
        graph.addUndirectedEdge("广州", "武汉", 30.0);
        graph.addUndirectedEdge("广州", "成都", 35.0);
        
        // 深圳的连接
        graph.addUndirectedEdge("深圳", "成都", 40.0);
        
        // 成都的连接
        graph.addUndirectedEdge("成都", "西安", 28.0);
        graph.addUndirectedEdge("成都", "武汉", 32.0);
        
        // 西安的连接
        graph.addUndirectedEdge("西安", "武汉", 26.0);
        
        return graph;
    }
    
    /**
     * 演示数据包路由
     */
    private static void demonstrateDataPacketRouting(Graph network) {
        System.out.println("【场景1：数据包路由优化】");
        System.out.println("需求：从北京服务器向深圳服务器发送数据包\n");
        
        PathResult result = DijkstraAlgorithm.findShortestPath(network, "北京", "深圳");
        
        System.out.println(result);
        System.out.println("\n技术分析：");
        System.out.println("1. 直观路径：北京 → 上海 → 广州 → 深圳");
        System.out.println("   总延迟：15 + 22 + 5 = 42ms");
        System.out.println("2. 最优路径：" + result.getFormattedPath());
        System.out.printf("   总延迟：%.1fms%n", result.getDistance());
        System.out.println("\n通过最短路径算法，数据包选择延迟最小的路由，");
        System.out.println("提高了网络传输效率和用户体验。");
    }
    
    /**
     * 演示网络中心节点分析
     */
    private static void demonstrateNetworkCentrality(Graph network) {
        System.out.println("【场景2：网络中心节点分析】");
        System.out.println("需求：识别网络中的关键节点（中心性分析）\n");
        
        // 计算每个节点到其他所有节点的平均延迟
        System.out.println("各节点的网络中心性（平均延迟）：\n");
        
        for (String node : network.getNodes()) {
            Map<String, PathResult> paths = DijkstraAlgorithm.findAllShortestPaths(network, node);
            
            double totalDelay = 0;
            int reachableNodes = 0;
            
            for (PathResult path : paths.values()) {
                if (path.isPathExists()) {
                    totalDelay += path.getDistance();
                    reachableNodes++;
                }
            }
            
            double avgDelay = reachableNodes > 0 ? totalDelay / reachableNodes : 0;
            
            System.out.printf("%s: 平均延迟 %.2fms (到达 %d 个节点)%n",
                node, avgDelay, reachableNodes);
        }
        
        System.out.println("\n分析：");
        System.out.println("平均延迟越小的节点，说明它在网络中的位置越中心。");
        System.out.println("这样的节点适合作为：");
        System.out.println("- 主服务器/主节点");
        System.out.println("- CDN缓存节点");
        System.out.println("- 负载均衡器位置");
    }
    
    /**
     * 演示网络故障容错
     */
    private static void demonstrateNetworkFailover(Graph network) {
        System.out.println("【场景3：网络故障容错与备用路由】");
        System.out.println("需求：当主要路由节点故障时，找到备用路径\n");
        
        String source = "北京";
        String destination = "广州";
        
        // 正常情况
        System.out.println("正常情况：");
        PathResult normalPath = DijkstraAlgorithm.findShortestPath(network, source, destination);
        System.out.printf("路径: %s%n", normalPath.getFormattedPath());
        System.out.printf("延迟: %.1fms%n%n", normalPath.getDistance());
        
        // 模拟上海节点故障
        System.out.println("故障场景：上海节点故障");
        Graph backupNetwork = buildNetworkTopology();
        
        // 移除上海节点（通过创建新图但不添加上海相关的边）
        Graph faultTolerantNetwork = new Graph();
        for (String node : backupNetwork.getNodes()) {
            if (!node.equals("上海")) {
                faultTolerantNetwork.addNode(node);
            }
        }
        
        // 重新添加边，排除与上海相关的连接
        faultTolerantNetwork.addUndirectedEdge("北京", "西安", 20.0);
        faultTolerantNetwork.addUndirectedEdge("北京", "武汉", 25.0);
        faultTolerantNetwork.addUndirectedEdge("广州", "深圳", 5.0);
        faultTolerantNetwork.addUndirectedEdge("广州", "武汉", 30.0);
        faultTolerantNetwork.addUndirectedEdge("广州", "成都", 35.0);
        faultTolerantNetwork.addUndirectedEdge("深圳", "成都", 40.0);
        faultTolerantNetwork.addUndirectedEdge("成都", "西安", 28.0);
        faultTolerantNetwork.addUndirectedEdge("成都", "武汉", 32.0);
        faultTolerantNetwork.addUndirectedEdge("西安", "武汉", 26.0);
        
        PathResult backupPath = DijkstraAlgorithm.findShortestPath(
            faultTolerantNetwork, source, destination);
        
        if (backupPath.isPathExists()) {
            System.out.printf("备用路径: %s%n", backupPath.getFormattedPath());
            System.out.printf("延迟: %.1fms%n", backupPath.getDistance());
            System.out.printf("延迟增加: %.1fms%n%n", 
                backupPath.getDistance() - normalPath.getDistance());
        }
        
        System.out.println("分析：");
        System.out.println("当网络节点发生故障时，Dijkstra算法自动找到备用路径，");
        System.out.println("确保网络的高可用性和容错能力。这在以下场景中至关重要：");
        System.out.println("- 互联网骨干网络");
        System.out.println("- 企业内网");
        System.out.println("- 云服务网络");
        System.out.println("- 物联网(IoT)网络");
    }
}
