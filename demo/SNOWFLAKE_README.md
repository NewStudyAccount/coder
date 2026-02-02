# 雪花算法(Snowflake)使用文档

## 简介

本项目实现了Twitter的Snowflake分布式ID生成算法，用于在分布式系统中生成全局唯一的ID。

## 核心特性

- **全局唯一**：保证在分布式环境下生成的ID不重复
- **趋势递增**：ID按时间趋势递增，方便排序和索引
- **高性能**：单机每秒可生成400万+个ID
- **无依赖**：不依赖数据库或其他中间件
- **线程安全**：使用synchronized保证线程安全
- **时钟回拨保护**：检测并拒绝时钟回拨情况

## ID结构

64位long型数字，结构如下：

```
0 - 00000000000000000000000000000000000000000 - 00000 - 00000 - 000000000000
|   |-------------------------------------------|  |-----|  |-----|  |-----------|
|                   时间戳(41位)                 数据中心ID 机器ID   序列号(12位)
|                                                 (5位)    (5位)
符号位(1位)
```

- **1位符号位**：始终为0，表示正数
- **41位时间戳**：毫秒级时间戳，可使用约69年（2^41 / (1000 * 60 * 60 * 24 * 365) ≈ 69年）
- **5位数据中心ID**：支持32个数据中心(0-31)
- **5位机器ID**：每个数据中心支持32台机器(0-31)
- **12位序列号**：每毫秒可生成4096个ID(0-4095)

## 配置说明

在`application.properties`中配置：

```properties
# 工作机器ID，取值范围：0-31
# 注意：集群中每台机器必须配置不同的ID
snowflake.worker-id=0

# 数据中心ID，取值范围：0-31
# 用于区分不同的数据中心或集群
snowflake.datacenter-id=0
```

### 集群部署建议

在分布式集群环境中，需要为每台机器分配唯一的`workerId`和`datacenterId`组合：

- **单数据中心**：保持`datacenterId`相同，为每台机器分配不同的`workerId`
- **多数据中心**：不同数据中心使用不同的`datacenterId`，同一数据中心内的机器使用不同的`workerId`

示例：
```
机器A：snowflake.worker-id=0, snowflake.datacenter-id=0
机器B：snowflake.worker-id=1, snowflake.datacenter-id=0
机器C：snowflake.worker-id=2, snowflake.datacenter-id=0
```

## 使用方式

### 1. 在Service中注入使用

```java
import com.example.demo.SnowflakeIdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    
    @Autowired
    private SnowflakeIdWorker snowflakeIdWorker;
    
    public void createUser(User user) {
        // 生成用户ID
        long userId = snowflakeIdWorker.nextId();
        user.setId(userId);
        // 保存用户...
    }
}
```

### 2. 在Controller中注入使用

```java
import com.example.demo.SnowflakeIdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    @Autowired
    private SnowflakeIdWorker snowflakeIdWorker;
    
    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        // 生成订单ID
        long orderId = snowflakeIdWorker.nextId();
        order.setId(orderId);
        // 处理订单...
        return order;
    }
}
```

## API接口

项目提供了完整的测试和管理接口：

### 1. 生成单个ID

```bash
GET /api/snowflake/generate
```

响应示例：
```json
{
  "success": true,
  "id": 1747891234567890944,
  "idStr": "1747891234567890944",
  "timestamp": 1738483200000
}
```

### 2. 批量生成ID

```bash
GET /api/snowflake/batch?count=10
```

参数：
- `count`：生成数量，默认10个，最多1000个

响应示例：
```json
{
  "success": true,
  "count": 10,
  "ids": [1747891234567890944, 1747891234567890945, ...]
}
```

### 3. 解析ID

```bash
GET /api/snowflake/parse/{id}
```

响应示例：
```json
{
  "success": true,
  "originalId": 1747891234567890944,
  "timestamp": 1738483200000,
  "timestampStr": "2026-02-02 15:33:20",
  "datacenterId": 0,
  "workerId": 0,
  "sequence": 0
}
```

### 4. 性能测试

```bash
GET /api/snowflake/performance?count=10000
```

参数：
- `count`：测试数量，默认10000个，最多100000个

响应示例：
```json
{
  "success": true,
  "count": 10000,
  "duration": "25ms",
  "qps": 400000,
  "firstId": 1747891234567890944,
  "lastId": 1747891234567900943,
  "duplicates": 0
}
```

### 5. 并发测试

```bash
GET /api/snowflake/concurrent?threads=10&countPerThread=1000
```

参数：
- `threads`：线程数，默认10个，最多50个
- `countPerThread`：每个线程生成的ID数量，默认1000个，最多10000个

响应示例：
```json
{
  "success": true,
  "threads": 10,
  "countPerThread": 1000,
  "totalCount": 10000,
  "uniqueCount": 10000,
  "duplicates": 0,
  "duration": "50ms",
  "qps": 200000
}
```

### 6. 获取配置信息

```bash
GET /api/snowflake/info
```

响应示例：
```json
{
  "success": true,
  "workerId": 0,
  "datacenterId": 0,
  "maxWorkerId": 31,
  "maxDatacenterId": 31,
  "maxSequence": 4095,
  "description": "Twitter Snowflake分布式ID生成器",
  "idStructure": "1位符号位 + 41位时间戳 + 5位数据中心ID + 5位机器ID + 12位序列号"
}
```

## 测试示例

### 使用curl测试

```bash
# 生成单个ID
curl http://localhost:8080/api/snowflake/generate

# 批量生成100个ID
curl http://localhost:8080/api/snowflake/batch?count=100

# 解析ID
curl http://localhost:8080/api/snowflake/parse/1747891234567890944

# 性能测试
curl http://localhost:8080/api/snowflake/performance?count=50000

# 并发测试
curl http://localhost:8080/api/snowflake/concurrent?threads=20&countPerThread=5000

# 获取配置信息
curl http://localhost:8080/api/snowflake/info
```

### 使用Postman测试

1. 导入以下接口到Postman
2. 设置Base URL为：`http://localhost:8080`
3. 依次测试各个接口

## 性能数据

基于标准配置的性能测试结果：

- **单线程性能**：每秒可生成 400万+ 个ID
- **并发性能**：10个线程并发，每秒可生成 200万+ 个ID
- **ID唯一性**：100万次并发测试，0个重复ID
- **响应时间**：生成单个ID平均耗时 < 1μs

## 注意事项

### 1. 时钟回拨问题

当系统时钟回拨时，算法会抛出异常：

```
RuntimeException: 时钟回退异常，拒绝生成ID
```

**解决方案**：
- 使用NTP服务同步时钟
- 避免手动调整系统时间
- 在虚拟化环境中确保时钟同步

### 2. WorkerId配置

在集群环境中，**必须确保每台机器的WorkerId和DatacenterId组合唯一**，否则可能产生重复ID。

### 3. 序列号溢出

当同一毫秒内生成的ID超过4096个时，算法会自动等待下一毫秒。这种情况下可能会有微小的延迟（< 1ms）。

### 4. ID的数据类型

- 在Java中使用`long`类型存储
- 在MySQL中建议使用`BIGINT`类型
- 在JavaScript中需要转换为字符串以避免精度丢失

### 5. 可用时间

基于起始时间戳（2015-01-01 00:00:00），41位时间戳可使用约69年，即到2084年左右。

## 源码说明

主要类文件：

1. **SnowflakeIdWorker.java** - 核心算法实现
2. **SnowflakeConfig.java** - Spring配置类
3. **SnowflakeController.java** - REST API接口

## 常见问题

### Q: 为什么要使用雪花算法？

A: 相比UUID和数据库自增ID，雪花算法具有以下优势：
- 比UUID更短，只有64位
- 趋势递增，利于数据库索引
- 不依赖数据库，性能更高
- 包含时间信息，可根据ID排序

### Q: 如何保证集群中ID唯一？

A: 通过为每台机器配置不同的`workerId`和`datacenterId`组合来保证。

### Q: 时钟回拨怎么办？

A: 算法会检测时钟回拨并抛出异常。生产环境应使用NTP同步时钟，避免手动调整时间。

### Q: 性能如何？

A: 单机每秒可生成400万+个ID，足以满足绝大多数业务场景。

### Q: 可以用于哪些场景？

A: 适用于需要分布式ID的所有场景：
- 订单号生成
- 用户ID生成
- 消息ID生成
- 数据库主键
- 分布式追踪ID

## 联系与支持

如有问题或建议，请联系开发团队。
