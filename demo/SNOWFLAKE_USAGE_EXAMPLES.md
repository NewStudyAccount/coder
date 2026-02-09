# 雪花算法使用示例

本文档提供雪花算法在 Java 和 JavaScript 中的详细使用示例。

## 目录
- [Java 使用示例](#java-使用示例)
- [JavaScript 使用示例](#javascript-使用示例)
- [实际应用场景](#实际应用场景)
- [性能测试](#性能测试)
- [常见问题](#常见问题)

---

## Java 使用示例

### 1. 基础使用

#### 在 Spring Boot Service 中使用

```java
import com.example.demo.SnowflakeIdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    
    @Autowired
    private SnowflakeIdWorker snowflakeIdWorker;
    
    public User createUser(String username, String email) {
        User user = new User();
        
        // 生成唯一用户ID
        long userId = snowflakeIdWorker.nextId();
        user.setId(userId);
        user.setUsername(username);
        user.setEmail(email);
        
        // 保存用户到数据库
        userRepository.save(user);
        
        return user;
    }
}
```

#### 在 Controller 中使用

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
    public Order createOrder(@RequestBody OrderRequest request) {
        Order order = new Order();
        
        // 生成订单ID
        long orderId = snowflakeIdWorker.nextId();
        order.setId(orderId);
        order.setUserId(request.getUserId());
        order.setAmount(request.getAmount());
        
        // 处理订单逻辑...
        orderService.save(order);
        
        return order;
    }
}
```

### 2. 批量生成ID

```java
@Service
public class BatchService {
    
    @Autowired
    private SnowflakeIdWorker snowflakeIdWorker;
    
    public List<Long> generateBatchIds(int count) {
        List<Long> ids = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ids.add(snowflakeIdWorker.nextId());
        }
        return ids;
    }
    
    public void batchInsertProducts(List<Product> products) {
        for (Product product : products) {
            // 为每个产品生成唯一ID
            product.setId(snowflakeIdWorker.nextId());
        }
        productRepository.saveAll(products);
    }
}
```

### 3. 解析ID信息

```java
@Service
public class IdAnalysisService {
    
    @Autowired
    private SnowflakeIdWorker snowflakeIdWorker;
    
    public void analyzeId(long id) {
        SnowflakeIdWorker.SnowflakeIdInfo info = snowflakeIdWorker.parseId(id);
        
        System.out.println("ID: " + info.getId());
        System.out.println("时间戳: " + info.getTimestamp());
        System.out.println("数据中心ID: " + info.getDatacenterId());
        System.out.println("机器ID: " + info.getWorkerId());
        System.out.println("序列号: " + info.getSequence());
    }
}
```

### 4. 配置文件设置

在 `application.properties` 或 `application.yml` 中配置：

```properties
# application.properties
snowflake.worker-id=1
snowflake.datacenter-id=0
```

或

```yaml
# application.yml
snowflake:
  worker-id: 1
  datacenter-id: 0
```

### 5. 数据库实体类示例

```java
import javax.persistence.*;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    // 使用雪花算法生成的ID，不使用数据库自增
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String username;
    
    @Column(nullable = false)
    private String email;
    
    // Getters and Setters...
}
```

---

## JavaScript 使用示例

### 1. 基础使用

#### 导入方式

```javascript
// 方式1：使用默认导出（推荐）
import Snowflake from '@/utils/snowflake.js';

// 生成单个ID
const id = Snowflake.generate();
console.log('生成的ID:', id);

// 方式2：导入类和实例
import { SnowflakeIdWorker, defaultSnowflake } from '@/utils/snowflake.js';

// 使用默认实例
const id = defaultSnowflake.nextIdStr();
console.log('生成的ID:', id);
```

### 2. 在 Vue 组件中使用

#### 示例1：用户注册组件

```vue
<template>
  <div class="register-form">
    <h2>用户注册</h2>
    <form @submit.prevent="handleRegister">
      <input v-model="username" placeholder="用户名" required />
      <input v-model="email" type="email" placeholder="邮箱" required />
      <input v-model="password" type="password" placeholder="密码" required />
      <button type="submit">注册</button>
    </form>
  </div>
</template>

<script>
import Snowflake from '@/utils/snowflake.js';
import axios from 'axios';

export default {
  data() {
    return {
      username: '',
      email: '',
      password: ''
    };
  },
  methods: {
    async handleRegister() {
      try {
        // 生成用户ID
        const userId = Snowflake.generate();
        
        const userData = {
          id: userId,
          username: this.username,
          email: this.email,
          password: this.password
        };
        
        // 发送注册请求
        const response = await axios.post('/api/users/register', userData);
        
        this.$message.success('注册成功！');
        this.$router.push('/login');
      } catch (error) {
        this.$message.error('注册失败：' + error.message);
      }
    }
  }
};
</script>
```

#### 示例2：订单创建组件

```vue
<template>
  <div class="order-create">
    <h2>创建订单</h2>
    <div class="order-info">
      <p>订单ID: {{ orderId }}</p>
      <p>创建时间: {{ orderTime }}</p>
    </div>
    <button @click="createOrder">生成新订单</button>
  </div>
</template>

<script>
import Snowflake from '@/utils/snowflake.js';

export default {
  data() {
    return {
      orderId: '',
      orderTime: ''
    };
  },
  methods: {
    createOrder() {
      // 生成订单ID
      this.orderId = Snowflake.generate();
      this.orderTime = new Date().toLocaleString('zh-CN');
      
      // 解析ID信息
      const info = Snowflake.parse(this.orderId);
      console.log('订单详情:', info);
      
      // 提交订单到后端...
      this.submitOrder();
    },
    async submitOrder() {
      try {
        const orderData = {
          id: this.orderId,
          userId: this.$store.state.user.id,
          items: this.cartItems,
          totalAmount: this.totalAmount
        };
        
        await this.$axios.post('/api/orders', orderData);
        this.$message.success('订单创建成功！');
      } catch (error) {
        this.$message.error('创建订单失败');
      }
    }
  },
  mounted() {
    // 页面加载时自动生成订单ID
    this.createOrder();
  }
};
</script>
```

### 3. 批量生成ID

```javascript
import Snowflake from '@/utils/snowflake.js';

// 批量生成10个ID
const ids = Snowflake.batchGenerate(10);
console.log('批量生成的ID:', ids);

// 为数组中的每个对象生成ID
const products = [
  { name: '商品A', price: 100 },
  { name: '商品B', price: 200 },
  { name: '商品C', price: 300 }
];

products.forEach(product => {
  product.id = Snowflake.generate();
});

console.log('带ID的商品:', products);
```

### 4. 解析ID信息

```javascript
import Snowflake from '@/utils/snowflake.js';

const id = Snowflake.generate();
console.log('生成的ID:', id);

// 解析ID
const info = Snowflake.parse(id);
console.log('ID信息:', info);
// 输出:
// {
//   id: "1747891234567890944",
//   timestamp: 1738483200000,
//   timestampStr: "2026/2/2 15:33:20",
//   datacenterId: 0,
//   workerId: 0,
//   sequence: 0
// }
```

### 5. 创建自定义实例

```javascript
import Snowflake from '@/utils/snowflake.js';

// 创建不同配置的实例
const worker1 = Snowflake.createInstance(1, 0);  // 机器1
const worker2 = Snowflake.createInstance(2, 0);  // 机器2

// 使用不同实例生成ID
const id1 = worker1.nextIdStr();
const id2 = worker2.nextIdStr();

console.log('机器1生成的ID:', id1);
console.log('机器2生成的ID:', id2);
```

### 6. 性能测试

```javascript
import Snowflake from '@/utils/snowflake.js';

// 执行性能测试
const result = Snowflake.performanceTest(10000);
console.log('性能测试结果:', result);
// 输出:
// {
//   count: 10000,
//   duration: "25ms",
//   qps: 400000,
//   firstId: "1747891234567890944",
//   lastId: "1747891234567900943",
//   uniqueCount: 10000,
//   duplicates: 0
// }
```

### 7. 在 Vuex Store 中使用

```javascript
// store/modules/order.js
import Snowflake from '@/utils/snowflake.js';

export default {
  namespaced: true,
  state: {
    orders: []
  },
  mutations: {
    ADD_ORDER(state, order) {
      state.orders.push(order);
    }
  },
  actions: {
    createOrder({ commit }, orderData) {
      // 生成订单ID
      const orderId = Snowflake.generate();
      
      const order = {
        id: orderId,
        ...orderData,
        createdAt: new Date().toISOString()
      };
      
      commit('ADD_ORDER', order);
      
      // 保存到后端
      return this.$axios.post('/api/orders', order);
    }
  }
};
```

---

## 实际应用场景

### 1. 用户系统

```java
// Java - 用户注册
@Service
public class UserService {
    @Autowired
    private SnowflakeIdWorker snowflake;
    
    public User register(UserDTO dto) {
        User user = new User();
        user.setId(snowflake.nextId());  // 生成用户ID
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setCreateTime(new Date());
        return userRepository.save(user);
    }
}
```

```javascript
// JavaScript - 用户注册
import Snowflake from '@/utils/snowflake.js';

async function registerUser(username, email, password) {
  const userId = Snowflake.generate();
  
  const response = await fetch('/api/users', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      id: userId,
      username,
      email,
      password
    })
  });
  
  return response.json();
}
```

### 2. 订单系统

```java
// Java - 创建订单
@Service
public class OrderService {
    @Autowired
    private SnowflakeIdWorker snowflake;
    
    public Order createOrder(Long userId, List<OrderItem> items) {
        Order order = new Order();
        order.setId(snowflake.nextId());  // 订单ID
        order.setUserId(userId);
        order.setOrderNo("ORD" + snowflake.nextId());  // 订单编号
        
        for (OrderItem item : items) {
            item.setId(snowflake.nextId());  // 订单项ID
            item.setOrderId(order.getId());
        }
        
        order.setItems(items);
        return orderRepository.save(order);
    }
}
```

### 3. 消息系统

```java
// Java - 发送消息
@Service
public class MessageService {
    @Autowired
    private SnowflakeIdWorker snowflake;
    
    public Message sendMessage(Long senderId, Long receiverId, String content) {
        Message message = new Message();
        message.setId(snowflake.nextId());
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setContent(content);
        message.setSentTime(new Date());
        
        messageRepository.save(message);
        // 发送到消息队列...
        return message;
    }
}
```

### 4. 文件上传

```javascript
// JavaScript - 文件上传
import Snowflake from '@/utils/snowflake.js';

async function uploadFile(file) {
  const fileId = Snowflake.generate();
  const formData = new FormData();
  
  formData.append('file', file);
  formData.append('fileId', fileId);
  formData.append('filename', file.name);
  
  const response = await fetch('/api/upload', {
    method: 'POST',
    body: formData
  });
  
  return {
    fileId,
    url: response.data.url
  };
}
```

---

## 性能测试

### Java 性能测试

```java
@Test
public void performanceTest() {
    SnowflakeIdWorker snowflake = new SnowflakeIdWorker(0, 0);
    int count = 100000;
    Set<Long> ids = new HashSet<>();
    
    long startTime = System.currentTimeMillis();
    
    for (int i = 0; i < count; i++) {
        ids.add(snowflake.nextId());
    }
    
    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;
    
    System.out.println("生成数量: " + count);
    System.out.println("耗时: " + duration + "ms");
    System.out.println("QPS: " + (count * 1000 / duration));
    System.out.println("唯一数量: " + ids.size());
    System.out.println("重复数量: " + (count - ids.size()));
}
```

### JavaScript 性能测试

```javascript
import Snowflake from '@/utils/snowflake.js';

function performanceTest() {
  const result = Snowflake.performanceTest(100000);
  console.log('性能测试结果:', result);
}

// 或自定义测试
function customPerformanceTest() {
  const count = 100000;
  const ids = new Set();
  const startTime = Date.now();
  
  for (let i = 0; i < count; i++) {
    ids.add(Snowflake.generate());
  }
  
  const endTime = Date.now();
  const duration = endTime - startTime;
  
  console.log({
    count,
    duration: `${duration}ms`,
    qps: Math.floor((count * 1000) / duration),
    uniqueCount: ids.size,
    duplicates: count - ids.size
  });
}
```

---

## 常见问题

### Q1: 如何在分布式环境中使用？

**A:** 在集群中为每台机器分配不同的 `workerId` 和 `datacenterId`：

```properties
# 机器A
snowflake.worker-id=0
snowflake.datacenter-id=0

# 机器B
snowflake.worker-id=1
snowflake.datacenter-id=0

# 机器C
snowflake.worker-id=2
snowflake.datacenter-id=0
```

### Q2: JavaScript 中为什么要使用字符串？

**A:** JavaScript 的 Number 类型只能安全表示到 `2^53-1`，雪花算法生成的是 64 位整数，会超出精度范围。使用 BigInt 处理后转为字符串传输可避免精度丢失。

### Q3: 如何避免时钟回拨问题？

**A:** 
- 使用 NTP 服务同步系统时钟
- 避免手动调整系统时间
- 在虚拟化环境中确保时钟同步
- 生产环境定期监控系统时间

### Q4: 性能如何？

**A:** 
- Java: 单机 400万+ QPS
- JavaScript: 单机 300万+ QPS（浏览器环境）
- 满足绝大多数业务场景需求

### Q5: 可以用作数据库主键吗？

**A:** 完全可以！雪花ID非常适合作为数据库主键：
- 全局唯一
- 趋势递增，利于索引
- 比 UUID 更短，性能更好

```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY,  -- 使用雪花ID
    username VARCHAR(50),
    email VARCHAR(100),
    created_at TIMESTAMP
);
```

---

## 总结

雪花算法是一个强大的分布式ID生成解决方案，适用于：
- 分布式系统
- 微服务架构
- 高并发场景
- 需要趋势递增ID的场景

本文档提供的示例覆盖了大部分使用场景，你可以根据实际需求进行调整和扩展。
