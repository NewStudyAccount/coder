# Java 集合框架使用说明

## 目录
1. [集合框架概述](#集合框架概述)
2. [核心接口](#核心接口)
3. [List 接口](#list-接口)
4. [Set 接口](#set-接口)
5. [Queue 接口](#queue-接口)
6. [Map 接口](#map-接口)
7. [实际使用场景案例](#实际使用场景案例)
8. [性能对比](#性能对比)
9. [最佳实践](#最佳实践)

---

## 集合框架概述

Java集合框架(Java Collections Framework, JCF)是一套用于存储和操作对象组的统一架构。它提供了接口、实现类和算法，使得数据操作更加高效和标准化。

### 集合框架的主要优势
- **统一的接口**: 所有集合类都实现了标准接口
- **高性能**: 针对不同场景优化的数据结构
- **互操作性**: 不同集合类型之间可以轻松转换
- **可扩展性**: 易于自定义集合实现

### 集合框架层次结构

```
Collection (接口)
├── List (接口)
│   ├── ArrayList (实现类)
│   ├── LinkedList (实现类)
│   └── Vector (实现类)
│       └── Stack (实现类)
├── Set (接口)
│   ├── HashSet (实现类)
│   ├── LinkedHashSet (实现类)
│   └── TreeSet (实现类)
└── Queue (接口)
    ├── PriorityQueue (实现类)
    ├── LinkedList (实现类)
    └── Deque (接口)
        └── ArrayDeque (实现类)

Map (接口)
├── HashMap (实现类)
├── LinkedHashMap (实现类)
├── TreeMap (实现类)
└── Hashtable (实现类)
    └── Properties (实现类)
```

---

## 核心接口

### Collection 接口
所有集合类的根接口，定义了集合的基本操作。

```java
public interface Collection<E> extends Iterable<E> {
    // 基本操作
    boolean add(E e);
    boolean remove(Object o);
    boolean contains(Object o);
    int size();
    boolean isEmpty();
    void clear();
    
    // 批量操作
    boolean addAll(Collection<? extends E> c);
    boolean removeAll(Collection<?> c);
    boolean retainAll(Collection<?> c);
    
    // 数组操作
    Object[] toArray();
    <T> T[] toArray(T[] a);
}
```

### Iterable 接口
使集合可以使用增强for循环遍历。

```java
public interface Iterable<T> {
    Iterator<T> iterator();
    
    // Java 8 新增
    default void forEach(Consumer<? super T> action) {
        for (T t : this) {
            action.accept(t);
        }
    }
}
```

---

## List 接口

List是有序集合，允许重复元素，可以通过索引访问元素。

### ArrayList - 动态数组

**特点:**
- 基于动态数组实现
- 随机访问速度快 O(1)
- 插入/删除慢 O(n)
- 非线程安全

**使用场景:**
- 需要频繁随机访问元素
- 元素数量变化不大
- 不需要频繁插入/删除

**代码示例:**

```java
import java.util.ArrayList;
import java.util.List;

public class ArrayListExample {
    public static void main(String[] args) {
        // 创建ArrayList
        List<String> fruits = new ArrayList<>();
        
        // 添加元素
        fruits.add("苹果");
        fruits.add("香蕉");
        fruits.add("橙子");
        
        // 指定位置插入
        fruits.add(1, "草莓");
        
        // 访问元素
        String first = fruits.get(0);
        System.out.println("第一个水果: " + first);
        
        // 修改元素
        fruits.set(0, "西瓜");
        
        // 删除元素
        fruits.remove(2);
        fruits.remove("香蕉");
        
        // 遍历
        for (String fruit : fruits) {
            System.out.println(fruit);
        }
        
        // Java 8 Lambda遍历
        fruits.forEach(fruit -> System.out.println(fruit));
        
        // 获取大小
        System.out.println("水果数量: " + fruits.size());
        
        // 检查是否包含
        boolean hasApple = fruits.contains("苹果");
        
        // 清空
        fruits.clear();
    }
}
```

**实际案例 - 学生成绩管理:**

```java
public class StudentGradeManager {
    private List<Integer> grades = new ArrayList<>();
    
    // 添加成绩
    public void addGrade(int grade) {
        if (grade >= 0 && grade <= 100) {
            grades.add(grade);
        }
    }
    
    // 计算平均分
    public double getAverage() {
        if (grades.isEmpty()) return 0;
        int sum = 0;
        for (int grade : grades) {
            sum += grade;
        }
        return (double) sum / grades.size();
    }
    
    // 获取最高分
    public int getHighest() {
        if (grades.isEmpty()) return 0;
        int max = grades.get(0);
        for (int grade : grades) {
            if (grade > max) max = grade;
        }
        return max;
    }
    
    // 统计及格人数
    public long countPassed() {
        return grades.stream()
                     .filter(grade -> grade >= 60)
                     .count();
    }
}
```

### LinkedList - 双向链表

**特点:**
- 基于双向链表实现
- 插入/删除快 O(1)
- 随机访问慢 O(n)
- 非线程安全
- 同时实现了List和Deque接口

**使用场景:**
- 需要频繁插入/删除元素
- 不需要频繁随机访问
- 需要队列或栈的功能

**代码示例:**

```java
import java.util.LinkedList;

public class LinkedListExample {
    public static void main(String[] args) {
        LinkedList<String> tasks = new LinkedList<>();
        
        // 作为List使用
        tasks.add("任务1");
        tasks.add("任务2");
        tasks.addFirst("紧急任务");  // 添加到开头
        tasks.addLast("低优先级任务"); // 添加到末尾
        
        // 作为Queue使用
        String first = tasks.poll();  // 移除并返回第一个元素
        tasks.offer("新任务");        // 添加到末尾
        
        // 作为Stack使用
        tasks.push("栈顶任务");       // 添加到开头
        String top = tasks.pop();     // 移除并返回第一个元素
        
        // 查看首尾元素(不移除)
        String head = tasks.peek();
        String tail = tasks.peekLast();
    }
}
```

**实际案例 - 浏览器历史记录:**

```java
public class BrowserHistory {
    private LinkedList<String> history = new LinkedList<>();
    private int currentIndex = -1;
    
    // 访问新页面
    public void visit(String url) {
        // 移除当前位置之后的所有历史
        while (history.size() > currentIndex + 1) {
            history.removeLast();
        }
        history.add(url);
        currentIndex++;
    }
    
    // 后退
    public String back() {
        if (currentIndex > 0) {
            currentIndex--;
            return history.get(currentIndex);
        }
        return null;
    }
    
    // 前进
    public String forward() {
        if (currentIndex < history.size() - 1) {
            currentIndex++;
            return history.get(currentIndex);
        }
        return null;
    }
    
    // 获取当前页面
    public String getCurrentPage() {
        if (currentIndex >= 0 && currentIndex < history.size()) {
            return history.get(currentIndex);
        }
        return null;
    }
}
```

### Vector - 线程安全的动态数组

**特点:**
- 与ArrayList类似，但线程安全
- 所有方法都是同步的
- 性能较ArrayList差
- 已过时，建议使用Collections.synchronizedList()

---

## Set 接口

Set是不允许重复元素的集合，不保证元素顺序。

### HashSet - 基于哈希表

**特点:**
- 基于HashMap实现
- 不保证顺序
- 允许null元素
- 查找、添加、删除 O(1)
- 非线程安全

**使用场景:**
- 需要快速查找
- 不需要排序
- 去重操作

**代码示例:**

```java
import java.util.HashSet;
import java.util.Set;

public class HashSetExample {
    public static void main(String[] args) {
        Set<String> uniqueNames = new HashSet<>();
        
        // 添加元素
        uniqueNames.add("张三");
        uniqueNames.add("李四");
        uniqueNames.add("张三");  // 重复元素不会被添加
        
        System.out.println("集合大小: " + uniqueNames.size()); // 输出: 2
        
        // 检查是否包含
        boolean hasZhangSan = uniqueNames.contains("张三");
        
        // 删除元素
        uniqueNames.remove("李四");
        
        // 遍历
        for (String name : uniqueNames) {
            System.out.println(name);
        }
    }
}
```

**实际案例 - 去重统计:**

```java
public class UniqueWordCounter {
    // 统计文本中的唯一单词数
    public static int countUniqueWords(String text) {
        String[] words = text.toLowerCase()
                            .replaceAll("[^a-z\\s]", "")
                            .split("\\s+");
        
        Set<String> uniqueWords = new HashSet<>();
        for (String word : words) {
            if (!word.isEmpty()) {
                uniqueWords.add(word);
            }
        }
        
        return uniqueWords.size();
    }
    
    // 查找两个集合的交集
    public static Set<String> findCommonElements(Set<String> set1, Set<String> set2) {
        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        return intersection;
    }
    
    // 查找两个集合的并集
    public static Set<String> findUnion(Set<String> set1, Set<String> set2) {
        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);
        return union;
    }
    
    // 查找差集
    public static Set<String> findDifference(Set<String> set1, Set<String> set2) {
        Set<String> difference = new HashSet<>(set1);
        difference.removeAll(set2);
        return difference;
    }
}
```

### LinkedHashSet - 保持插入顺序的HashSet

**特点:**
- 继承自HashSet
- 维护插入顺序
- 性能略低于HashSet

**使用场景:**
- 需要去重且保持插入顺序

```java
import java.util.LinkedHashSet;
import java.util.Set;

public class LinkedHashSetExample {
    public static void main(String[] args) {
        Set<String> orderedSet = new LinkedHashSet<>();
        orderedSet.add("第一个");
        orderedSet.add("第二个");
        orderedSet.add("第三个");
        
        // 遍历时保持插入顺序
        orderedSet.forEach(System.out::println);
    }
}
```

### TreeSet - 有序集合

**特点:**
- 基于红黑树实现
- 元素自动排序
- 不允许null
- 添加、删除、查找 O(log n)

**使用场景:**
- 需要排序的集合
- 需要范围查询

**代码示例:**

```java
import java.util.TreeSet;
import java.util.Set;

public class TreeSetExample {
    public static void main(String[] args) {
        Set<Integer> sortedNumbers = new TreeSet<>();
        
        sortedNumbers.add(5);
        sortedNumbers.add(2);
        sortedNumbers.add(8);
        sortedNumbers.add(1);
        
        // 自动排序: 1, 2, 5, 8
        sortedNumbers.forEach(System.out::println);
        
        // TreeSet特有方法
        TreeSet<Integer> treeSet = new TreeSet<>(sortedNumbers);
        System.out.println("最小值: " + treeSet.first());
        System.out.println("最大值: " + treeSet.last());
        System.out.println("小于5的元素: " + treeSet.headSet(5));
        System.out.println("大于等于5的元素: " + treeSet.tailSet(5));
    }
}
```

**实际案例 - 排行榜系统:**

```java
public class LeaderboardSystem {
    // 使用TreeSet按分数降序排列
    private TreeSet<Player> leaderboard = new TreeSet<>((p1, p2) -> {
        int scoreCompare = Integer.compare(p2.getScore(), p1.getScore());
        if (scoreCompare != 0) return scoreCompare;
        return p1.getName().compareTo(p2.getName());
    });
    
    static class Player {
        private String name;
        private int score;
        
        public Player(String name, int score) {
            this.name = name;
            this.score = score;
        }
        
        public String getName() { return name; }
        public int score() { return score; }
        public void setScore(int score) { this.score = score; }
    }
    
    // 更新玩家分数
    public void updateScore(Player player, int newScore) {
        leaderboard.remove(player);
        player.setScore(newScore);
        leaderboard.add(player);
    }
    
    // 获取前N名
    public List<Player> getTopN(int n) {
        return leaderboard.stream()
                         .limit(n)
                         .collect(Collectors.toList());
    }
    
    // 获取玩家排名
    public int getRank(Player player) {
        int rank = 1;
        for (Player p : leaderboard) {
            if (p.equals(player)) return rank;
            rank++;
        }
        return -1;
    }
}
```

---

## Queue 接口

Queue是队列接口，遵循FIFO(先进先出)原则。

### PriorityQueue - 优先级队列

**特点:**
- 基于堆实现
- 元素按自然顺序或自定义比较器排序
- 不允许null
- 插入和删除 O(log n)

**使用场景:**
- 需要按优先级处理任务
- 需要最小/最大元素

**代码示例:**

```java
import java.util.PriorityQueue;
import java.util.Queue;

public class PriorityQueueExample {
    public static void main(String[] args) {
        // 默认是最小堆
        Queue<Integer> minHeap = new PriorityQueue<>();
        minHeap.offer(5);
        minHeap.offer(2);
        minHeap.offer(8);
        minHeap.offer(1);
        
        // 按从小到大顺序取出
        while (!minHeap.isEmpty()) {
            System.out.println(minHeap.poll()); // 1, 2, 5, 8
        }
        
        // 最大堆
        Queue<Integer> maxHeap = new PriorityQueue<>((a, b) -> b - a);
        maxHeap.offer(5);
        maxHeap.offer(2);
        maxHeap.offer(8);
        
        System.out.println(maxHeap.poll()); // 8
    }
}
```

**实际案例 - 任务调度系统:**

```java
public class TaskScheduler {
    static class Task implements Comparable<Task> {
        private String name;
        private int priority;  // 数字越小优先级越高
        private long deadline;
        
        public Task(String name, int priority, long deadline) {
            this.name = name;
            this.priority = priority;
            this.deadline = deadline;
        }
        
        @Override
        public int compareTo(Task other) {
            // 先按优先级，再按截止时间
            int priorityCompare = Integer.compare(this.priority, other.priority);
            if (priorityCompare != 0) return priorityCompare;
            return Long.compare(this.deadline, other.deadline);
        }
        
        public String getName() { return name; }
    }
    
    private PriorityQueue<Task> taskQueue = new PriorityQueue<>();
    
    // 添加任务
    public void addTask(Task task) {
        taskQueue.offer(task);
    }
    
    // 执行下一个任务
    public void executeNextTask() {
        Task task = taskQueue.poll();
        if (task != null) {
            System.out.println("执行任务: " + task.getName());
        }
    }
    
    // 查看下一个任务
    public Task peekNextTask() {
        return taskQueue.peek();
    }
}
```

### ArrayDeque - 双端队列

**特点:**
- 基于数组实现的双端队列
- 两端都可以添加/删除
- 性能优于LinkedList
- 不允许null

**使用场景:**
- 需要栈或队列功能
- 需要高性能的双端操作

**代码示例:**

```java
import java.util.ArrayDeque;
import java.util.Deque;

public class ArrayDequeExample {
    public static void main(String[] args) {
        Deque<String> deque = new ArrayDeque<>();
        
        // 作为栈使用
        deque.push("第一个");
        deque.push("第二个");
        System.out.println(deque.pop()); // 第二个
        
        // 作为队列使用
        deque.offer("A");
        deque.offer("B");
        System.out.println(deque.poll()); // A
        
        // 双端操作
        deque.offerFirst("开头");
        deque.offerLast("结尾");
        System.out.println(deque.pollFirst());
        System.out.println(deque.pollLast());
    }
}
```

---

## Map 接口

Map存储键值对,键唯一,值可以重复。

### HashMap - 基于哈希表

**特点:**
- 基于哈希表实现
- 允许null键和null值
- 不保证顺序
- 查找、添加、删除 O(1)
- 非线程安全

**使用场景:**
- 需要快速查找
- 键值对存储
- 缓存实现

**代码示例:**

```java
import java.util.HashMap;
import java.util.Map;

public class HashMapExample {
    public static void main(String[] args) {
        Map<String, Integer> ageMap = new HashMap<>();
        
        // 添加元素
        ageMap.put("张三", 25);
        ageMap.put("李四", 30);
        ageMap.put("王五", 28);
        
        // 获取值
        Integer age = ageMap.get("张三");
        
        // 获取值,不存在返回默认值
        Integer defaultAge = ageMap.getOrDefault("赵六", 0);
        
        // 检查键是否存在
        boolean hasKey = ageMap.containsKey("张三");
        
        // 检查值是否存在
        boolean hasValue = ageMap.containsValue(25);
        
        // 删除
        ageMap.remove("李四");
        
        // 遍历方式1: entrySet
        for (Map.Entry<String, Integer> entry : ageMap.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        
        // 遍历方式2: keySet
        for (String key : ageMap.keySet()) {
            System.out.println(key + ": " + ageMap.get(key));
        }
        
        // 遍历方式3: values
        for (Integer value : ageMap.values()) {
            System.out.println(value);
        }
        
        // Java 8 forEach
        ageMap.forEach((k, v) -> System.out.println(k + ": " + v));
        
        // 如果键不存在则添加
        ageMap.putIfAbsent("新用户", 20);
        
        // 计算新值
        ageMap.compute("张三", (k, v) -> v + 1);
        
        // 合并值
        ageMap.merge("张三", 1, Integer::sum);
    }
}
```

**实际案例 - 单词频率统计:**

```java
public class WordFrequencyCounter {
    public static Map<String, Integer> countWordFrequency(String text) {
        Map<String, Integer> frequencyMap = new HashMap<>();
        
        String[] words = text.toLowerCase()
                            .replaceAll("[^a-z\\s]", "")
                            .split("\\s+");
        
        for (String word : words) {
            if (!word.isEmpty()) {
                frequencyMap.put(word, frequencyMap.getOrDefault(word, 0) + 1);
            }
        }
        
        return frequencyMap;
    }
    
    // 找出出现频率最高的N个单词
    public static List<Map.Entry<String, Integer>> getTopNWords(
            Map<String, Integer> frequencyMap, int n) {
        return frequencyMap.entrySet().stream()
                          .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                          .limit(n)
                          .collect(Collectors.toList());
    }
}
```

**实际案例 - 缓存实现:**

```java
public class SimpleCache<K, V> {
    private Map<K, CacheEntry<V>> cache = new HashMap<>();
    private long defaultTTL = 60000; // 默认1分钟过期
    
    static class CacheEntry<V> {
        V value;
        long expiryTime;
        
        CacheEntry(V value, long ttl) {
            this.value = value;
            this.expiryTime = System.currentTimeMillis() + ttl;
        }
        
        boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }
    
    // 添加到缓存
    public void put(K key, V value) {
        cache.put(key, new CacheEntry<>(value, defaultTTL));
    }
    
    // 添加到缓存并指定过期时间
    public void put(K key, V value, long ttl) {
        cache.put(key, new CacheEntry<>(value, ttl));
    }
    
    // 从缓存获取
    public V get(K key) {
        CacheEntry<V> entry = cache.get(key);
        if (entry == null) return null;
        
        if (entry.isExpired()) {
            cache.remove(key);
            return null;
        }
        
        return entry.value;
    }
    
    // 清理过期缓存
    public void cleanExpired() {
        cache.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
}
```

### LinkedHashMap - 保持插入顺序

**特点:**
- 继承自HashMap
- 维护插入顺序或访问顺序
- 性能略低于HashMap

**使用场景:**
- 需要保持插入顺序
- LRU缓存实现

**代码示例:**

```java
import java.util.LinkedHashMap;
import java.util.Map;

public class LinkedHashMapExample {
    public static void main(String[] args) {
        // 按插入顺序
        Map<String, Integer> insertOrderMap = new LinkedHashMap<>();
        insertOrderMap.put("First", 1);
        insertOrderMap.put("Second", 2);
        insertOrderMap.put("Third", 3);
        
        // 遍历时保持插入顺序
        insertOrderMap.forEach((k, v) -> System.out.println(k + ": " + v));
        
        // 按访问顺序(LRU)
        Map<String, Integer> accessOrderMap = new LinkedHashMap<>(16, 0.75f, true);
        accessOrderMap.put("A", 1);
        accessOrderMap.put("B", 2);
        accessOrderMap.put("C", 3);
        
        accessOrderMap.get("A"); // 访问A,A移到末尾
        // 顺序变为: B, C, A
    }
}
```

**实际案例 - LRU缓存:**

```java
public class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private int capacity;
    
    public LRUCache(int capacity) {
        super(capacity, 0.75f, true);
        this.capacity = capacity;
    }
    
    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;
    }
    
    public static void main(String[] args) {
        LRUCache<String, Integer> cache = new LRUCache<>(3);
        
        cache.put("A", 1);
        cache.put("B", 2);
        cache.put("C", 3);
        System.out.println(cache); // {A=1, B=2, C=3}
        
        cache.get("A"); // 访问A
        System.out.println(cache); // {B=2, C=3, A=1}
        
        cache.put("D", 4); // 超过容量,B被移除
        System.out.println(cache); // {C=3, A=1, D=4}
    }
}
```

### TreeMap - 有序Map

**特点:**
- 基于红黑树实现
- 键自动排序
- 不允许null键
- 操作时间复杂度 O(log n)

**使用场景:**
- 需要排序的键值对
- 需要范围查询

**代码示例:**

```java
import java.util.TreeMap;
import java.util.Map;

public class TreeMapExample {
    public static void main(String[] args) {
        TreeMap<Integer, String> treeMap = new TreeMap<>();
        
        treeMap.put(3, "Three");
        treeMap.put(1, "One");
        treeMap.put(2, "Two");
        treeMap.put(5, "Five");
        
        // 自动按键排序
        treeMap.forEach((k, v) -> System.out.println(k + ": " + v));
        
        // TreeMap特有方法
        System.out.println("第一个键: " + treeMap.firstKey());
        System.out.println("最后一个键: " + treeMap.lastKey());
        
        // 范围视图
        Map<Integer, String> subMap = treeMap.subMap(2, 5); // [2, 5)
        System.out.println("子映射: " + subMap);
        
        // 小于3的所有元素
        Map<Integer, String> headMap = treeMap.headMap(3);
        
        // 大于等于3的所有元素
        Map<Integer, String> tailMap = treeMap.tailMap(3);
    }
}
```

---

## 实际使用场景案例

### 案例1: 购物车系统

```java
public class ShoppingCart {
    // 使用Map存储商品ID和数量
    private Map<String, CartItem> items = new HashMap<>();
    
    static class CartItem {
        private String productId;
        private String productName;
        private double price;
        private int quantity;
        
        public CartItem(String productId, String productName, double price, int quantity) {
            this.productId = productId;
            this.productName = productName;
            this.price = price;
            this.quantity = quantity;
        }
        
        public double getSubtotal() {
            return price * quantity;
        }
        
        // getters and setters
        public String getProductId() { return productId; }
        public String getProductName() { return productName; }
        public double getPrice() { return price; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }
    
    // 添加商品
    public void addItem(String productId, String productName, double price, int quantity) {
        if (items.containsKey(productId)) {
            CartItem item = items.get(productId);
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            items.put(productId, new CartItem(productId, productName, price, quantity));
        }
    }
    
    // 移除商品
    public void removeItem(String productId) {
        items.remove(productId);
    }
    
    // 更新数量
    public void updateQuantity(String productId, int quantity) {
        if (items.containsKey(productId)) {
            if (quantity <= 0) {
                items.remove(productId);
            } else {
                items.get(productId).setQuantity(quantity);
            }
        }
    }
    
    // 计算总价
    public double getTotal() {
        return items.values().stream()
                   .mapToDouble(CartItem::getSubtotal)
                   .sum();
    }
    
    // 获取商品数量
    public int getItemCount() {
        return items.values().stream()
                   .mapToInt(CartItem::getQuantity)
                   .sum();
    }
    
    // 清空购物车
    public void clear() {
        items.clear();
    }
    
    // 获取所有商品
    public Collection<CartItem> getItems() {
        return items.values();
    }
}
```

### 案例2: 学生选课系统

```java
public class CourseRegistrationSystem {
    // 课程信息
    static class Course {
        private String courseId;
        private String courseName;
        private int capacity;
        private Set<String> enrolledStudents;
        
        public Course(String courseId, String courseName, int capacity) {
            this.courseId = courseId;
            this.courseName = courseName;
            this.capacity = capacity;
            this.enrolledStudents = new HashSet<>();
        }
        
        public boolean enroll(String studentId) {
            if (enrolledStudents.size() >= capacity) {
                return false; // 课程已满
            }
            return enrolledStudents.add(studentId);
        }
        
        public boolean drop(String studentId) {
            return enrolledStudents.remove(studentId);
        }
        
        public int getAvailableSeats() {
            return capacity - enrolledStudents.size();
        }
        
        // getters
        public String getCourseId() { return courseId; }
        public String getCourseName() { return courseName; }
        public Set<String> getEnrolledStudents() { return enrolledStudents; }
    }
    
    // 学生信息
    static class Student {
        private String studentId;
        private String name;
        private Set<String> courses;
        
        public Student(String studentId, String name) {
            this.studentId = studentId;
            this.name = name;
            this.courses = new HashSet<>();
        }
        
        public boolean addCourse(String courseId) {
            return courses.add(courseId);
        }
        
        public boolean dropCourse(String courseId) {
            return courses.remove(courseId);
        }
        
        // getters
        public String getStudentId() { return studentId; }
        public String getName() { return name; }
        public Set<String> getCourses() { return courses; }
    }
    
    private Map<String, Course> courses = new HashMap<>();
    private Map<String, Student> students = new HashMap<>();
    
    // 添加课程
    public void addCourse(Course course) {
        courses.put(course.getCourseId(), course);
    }
    
    // 添加学生
    public void addStudent(Student student) {
        students.put(student.getStudentId(), student);
    }
    
    // 学生选课
    public boolean registerCourse(String studentId, String courseId) {
        Student student = students.get(studentId);
        Course course = courses.get(courseId);
        
        if (student == null || course == null) {
            return false;
        }
        
        if (course.enroll(studentId)) {
            student.addCourse(courseId);
            return true;
        }
        
        return false;
    }
    
    // 学生退课
    public boolean dropCourse(String studentId, String courseId) {
        Student student = students.get(studentId);
        Course course = courses.get(courseId);
        
        if (student == null || course == null) {
            return false;
        }
        
        if (course.drop(studentId)) {
            student.dropCourse(courseId);
            return true;
        }
        
        return false;
    }
    
    // 查询学生的所有课程
    public List<Course> getStudentCourses(String studentId) {
        Student student = students.get(studentId);
        if (student == null) return new ArrayList<>();
        
        return student.getCourses().stream()
                     .map(courses::get)
                     .filter(course -> course != null)
                     .collect(Collectors.toList());
    }
    
    // 查询课程的所有学生
    public List<Student> getCourseStudents(String courseId) {
        Course course = courses.get(courseId);
        if (course == null) return new ArrayList<>();
        
        return course.getEnrolledStudents().stream()
                    .map(students::get)
                    .filter(student -> student != null)
                    .collect(Collectors.toList());
    }
}
```

### 案例3: 多级分类系统

```java
public class CategoryTree {
    static class Category {
        private String id;
        private String name;
        private String parentId;
        private List<Category> children;
        
        public Category(String id, String name, String parentId) {
            this.id = id;
            this.name = name;
            this.parentId = parentId;
            this.children = new ArrayList<>();
        }
        
        public void addChild(Category child) {
            children.add(child);
        }
        
        // getters
        public String getId() { return id; }
        public String getName() { return name; }
        public String getParentId() { return parentId; }
        public List<Category> getChildren() { return children; }
    }
    
    private Map<String, Category> categoryMap = new HashMap<>();
    private List<Category> rootCategories = new ArrayList<>();
    
    // 添加分类
    public void addCategory(Category category) {
        categoryMap.put(category.getId(), category);
        
        if (category.getParentId() == null) {
            // 根分类
            rootCategories.add(category);
        } else {
            // 子分类
            Category parent = categoryMap.get(category.getParentId());
            if (parent != null) {
                parent.addChild(category);
            }
        }
    }
    
    // 获取某个分类的所有子孙分类
    public List<Category> getAllDescendants(String categoryId) {
        List<Category> descendants = new ArrayList<>();
        Category category = categoryMap.get(categoryId);
        
        if (category != null) {
            collectDescendants(category, descendants);
        }
        
        return descendants;
    }
    
    private void collectDescendants(Category category, List<Category> result) {
        for (Category child : category.getChildren()) {
            result.add(child);
            collectDescendants(child, result);
        }
    }
    
    // 获取从根到某个分类的路径
    public List<Category> getPath(String categoryId) {
        List<Category> path = new ArrayList<>();
        Category category = categoryMap.get(categoryId);
        
        while (category != null) {
            path.add(0, category);
            category = categoryMap.get(category.getParentId());
        }
        
        return path;
    }
    
    // 打印树结构
    public void printTree() {
        for (Category root : rootCategories) {
            printCategory(root, 0);
        }
    }
    
    private void printCategory(Category category, int level) {
        String indent = "  ".repeat(level);
        System.out.println(indent + category.getName());
        
        for (Category child : category.getChildren()) {
            printCategory(child, level + 1);
        }
    }
}
```

### 案例4: 日志分析系统

```java
public class LogAnalyzer {
    static class LogEntry {
        private String timestamp;
        private String level;
        private String message;
        private String source;
        
        public LogEntry(String timestamp, String level, String message, String source) {
            this.timestamp = timestamp;
            this.level = level;
            this.message = message;
            this.source = source;
        }
        
        // getters
        public String getTimestamp() { return timestamp; }
        public String getLevel() { return level; }
        public String getMessage() { return message; }
        public String getSource() { return source; }
    }
    
    private List<LogEntry> logs = new ArrayList<>();
    
    // 添加日志
    public void addLog(LogEntry log) {
        logs.add(log);
    }
    
    // 按级别统计
    public Map<String, Long> countByLevel() {
        return logs.stream()
                  .collect(Collectors.groupingBy(
                      LogEntry::getLevel,
                      Collectors.counting()
                  ));
    }
    
    // 按来源统计
    public Map<String, Long> countBySource() {
        return logs.stream()
                  .collect(Collectors.groupingBy(
                      LogEntry::getSource,
                      Collectors.counting()
                  ));
    }
    
    // 查找包含特定关键词的日志
    public List<LogEntry> searchByKeyword(String keyword) {
        return logs.stream()
                  .filter(log -> log.getMessage().contains(keyword))
                  .collect(Collectors.toList());
    }
    
    // 查找特定级别的日志
    public List<LogEntry> filterByLevel(String level) {
        return logs.stream()
                  .filter(log -> log.getLevel().equals(level))
                  .collect(Collectors.toList());
    }
    
    // 查找错误日志的来源分布
    public Map<String, List<LogEntry>> getErrorsBySource() {
        return logs.stream()
                  .filter(log -> "ERROR".equals(log.getLevel()))
                  .collect(Collectors.groupingBy(LogEntry::getSource));
    }
    
    // 获取最近N条日志
    public List<LogEntry> getRecentLogs(int n) {
        int start = Math.max(0, logs.size() - n);
        return new ArrayList<>(logs.subList(start, logs.size()));
    }
}
```

---

## 性能对比

### 时间复杂度对比表

| 操作 | ArrayList | LinkedList | HashSet | TreeSet | HashMap | TreeMap |
|------|-----------|------------|---------|---------|---------|---------|
| 添加(末尾) | O(1) | O(1) | O(1) | O(log n) | O(1) | O(log n) |
| 添加(指定位置) | O(n) | O(n) | - | - | - | - |
| 删除(末尾) | O(1) | O(1) | O(1) | O(log n) | O(1) | O(log n) |
| 删除(指定位置) | O(n) | O(n) | - | - | - | - |
| 查找 | O(n) | O(n) | O(1) | O(log n) | O(1) | O(log n) |
| 随机访问 | O(1) | O(n) | - | - | O(1) | O(log n) |

### 空间复杂度

- **ArrayList**: O(n) - 需要连续内存空间
- **LinkedList**: O(n) - 每个节点额外存储前后指针
- **HashSet/HashMap**: O(n) - 需要额外空间存储哈希表
- **TreeSet/TreeMap**: O(n) - 树结构需要额外指针

### 选择建议

**使用ArrayList的场景:**
- 需要频繁随机访问
- 知道大致容量,避免扩容
- 遍历多于插入删除

**使用LinkedList的场景:**
- 频繁在头尾插入删除
- 不需要随机访问
- 内存不连续也可以

**使用HashSet的场景:**
- 需要快速查找和去重
- 不需要排序
- 元素hashCode分布均匀

**使用TreeSet的场景:**
- 需要排序的集合
- 需要范围查询
- 需要获取最大最小值

**使用HashMap的场景:**
- 需要快速查找键值对
- 不需要排序
- 键的hashCode分布均匀

**使用TreeMap的场景:**
- 需要按键排序
- 需要范围查询
- 需要获取最大最小键

---

## 最佳实践

### 1. 指定初始容量

```java
// 好的做法 - 避免多次扩容
List<String> list = new ArrayList<>(100);
Map<String, Integer> map = new HashMap<>(100);

// 不好的做法
List<String> list = new ArrayList<>(); // 默认容量10
```

### 2. 使用接口类型声明

```java
// 好的做法
List<String> list = new ArrayList<>();
Set<Integer> set = new HashSet<>();
Map<String, Object> map = new HashMap<>();

// 不好的做法
ArrayList<String> list = new ArrayList<>();
HashSet<Integer> set = new HashSet<>();
HashMap<String, Object> map = new HashMap<>();
```

### 3. 不可变集合

```java
// Java 9+
List<String> immutableList = List.of("A", "B", "C");
Set<Integer> immutableSet = Set.of(1, 2, 3);
Map<String, Integer> immutableMap = Map.of("A", 1, "B", 2);

// 早期版本
List<String> list = Arrays.asList("A", "B", "C");
List<String> unmodifiableList = Collections.unmodifiableList(list);
```

### 4. 线程安全的集合

```java
// 同步包装器
List<String> syncList = Collections.synchronizedList(new ArrayList<>());
Set<Integer> syncSet = Collections.synchronizedSet(new HashSet<>());
Map<String, Object> syncMap = Collections.synchronizedMap(new HashMap<>());

// 并发集合(推荐)
List<String> copyOnWriteList = new CopyOnWriteArrayList<>();
Set<Integer> concurrentSet = ConcurrentHashMap.newKeySet();
Map<String, Object> concurrentMap = new ConcurrentHashMap<>();
```

### 5. 集合转换

```java
// List转Set
List<String> list = Arrays.asList("A", "B", "C", "A");
Set<String> set = new HashSet<>(list); // 去重

// Set转List
Set<String> set = new HashSet<>(Arrays.asList("A", "B", "C"));
List<String> list = new ArrayList<>(set);

// 数组转List
String[] array = {"A", "B", "C"};
List<String> list = Arrays.asList(array);
// 或
List<String> list = new ArrayList<>(Arrays.asList(array));

// List转数组
List<String> list = Arrays.asList("A", "B", "C");
String[] array = list.toArray(new String[0]);
```

### 6. 使用Stream API

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);

// 过滤
List<Integer> evenNumbers = numbers.stream()
    .filter(n -> n % 2 == 0)
    .collect(Collectors.toList());

// 映射
List<String> strings = numbers.stream()
    .map(String::valueOf)
    .collect(Collectors.toList());

// 排序
List<Integer> sorted = numbers.stream()
    .sorted()
    .collect(Collectors.toList());

// 去重
List<Integer> distinct = numbers.stream()
    .distinct()
    .collect(Collectors.toList());

// 统计
long count = numbers.stream().count();
int sum = numbers.stream().mapToInt(Integer::intValue).sum();
double average = numbers.stream().mapToInt(Integer::intValue).average().orElse(0);
```

### 7. 集合工具类

```java
// Collections工具类
Collections.sort(list);                    // 排序
Collections.reverse(list);                 // 反转
Collections.shuffle(list);                 // 随机打乱
Collections.fill(list, "X");              // 填充
Collections.max(list);                     // 最大值
Collections.min(list);                     // 最小值
Collections.frequency(list, "A");         // 统计频率
Collections.swap(list, 0, 1);            // 交换元素

// Arrays工具类
Arrays.sort(array);                        // 排序
Arrays.fill(array, 0);                    // 填充
Arrays.equals(array1, array2);            // 比较
Arrays.copyOf(array, newLength);          // 复制
```

### 8. 避免常见陷阱

```java
// 陷阱1: Arrays.asList返回固定大小的List
List<String> list = Arrays.asList("A", "B");
// list.add("C"); // 抛出UnsupportedOperationException

// 解决方案
List<String> list = new ArrayList<>(Arrays.asList("A", "B"));
list.add("C"); // OK

// 陷阱2: 并发修改异常
List<String> list = new ArrayList<>(Arrays.asList("A", "B", "C"));
for (String s : list) {
    if (s.equals("B")) {
        // list.remove(s); // 抛出ConcurrentModificationException
    }
}

// 解决方案1: 使用Iterator
Iterator<String> it = list.iterator();
while (it.hasNext()) {
    if (it.next().equals("B")) {
        it.remove();
    }
}

// 解决方案2: 使用removeIf (Java 8+)
list.removeIf(s -> s.equals("B"));

// 陷阱3: HashMap的null键值
Map<String, String> map = new HashMap<>();
map.put(null, "value"); // OK
map.put("key", null);   // OK

// TreeMap不允许null键
Map<String, String> treeMap = new TreeMap<>();
// treeMap.put(null, "value"); // 抛出NullPointerException
```

### 9. 性能优化技巧

```java
// 1. 使用StringBuilder代替String拼接
StringBuilder sb = new StringBuilder();
for (String s : list) {
    sb.append(s);
}

// 2. 批量操作
list.addAll(otherList); // 比循环add快

// 3. 预分配容量
List<String> list = new ArrayList<>(expectedSize);

// 4. 使用EnumSet代替普通Set存储枚举
EnumSet<DayOfWeek> weekend = EnumSet.of(
    DayOfWeek.SATURDAY, 
    DayOfWeek.SUNDAY
);

// 5. 使用位集合存储大量布尔值
BitSet bitSet = new BitSet(1000000);
bitSet.set(100);
boolean value = bitSet.get(100);
```

---

## 总结

Java集合框架提供了丰富的数据结构来满足不同的需求：

1. **List**: 有序可重复,选择ArrayList或LinkedList
2. **Set**: 无序不重复,选择HashSet、LinkedHashSet或TreeSet
3. **Queue**: 队列操作,选择PriorityQueue或ArrayDeque
4. **Map**: 键值对,选择HashMap、LinkedHashMap或TreeMap

选择合适的集合类型需要考虑：
- 是否需要排序
- 是否允许重复
- 访问模式(随机访问vs顺序访问)
- 性能要求(时间vs空间)
- 是否需要线程安全

掌握集合框架是Java编程的基础,合理使用可以大大提高代码质量和性能。
