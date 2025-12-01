# 三大类设计模式原理、案例代码与注意点

---

## 一、创建型模式 —— 单例模式（Singleton）

### 原理
单例模式确保一个类只有一个实例，并提供全局访问点。常用于全局唯一资源管理，如配置、线程池等。

### 代码示例（Java，饿汉式单例）
```java
/**
 * 单例模式 - 饿汉式
 * 特点：类加载时即创建唯一实例，线程安全
 */
public class Singleton {
    // 唯一实例，类加载时初始化
    private static final Singleton INSTANCE = new Singleton();

    // 私有构造方法，防止外部new
    private Singleton() {}

    // 全局访问点
    public static Singleton getInstance() {
        return INSTANCE;
    }
}
```

### 代码注释
- `private static final Singleton INSTANCE`：静态常量，保证唯一性和线程安全。
- `private Singleton()`：私有构造，防止外部实例化。
- `getInstance()`：全局唯一访问点。

### 注意点
- 饿汉式线程安全，但如果实例创建开销大且未必用到，可能浪费资源。
- 懒汉式需注意多线程下的双重检查锁定写法。
- 单例不适合有状态（成员变量可变）的场景。

---

## 二、结构型模式 —— 适配器模式（Adapter）

### 原理
适配器模式将一个接口转换成客户端期望的另一个接口，使原本接口不兼容的类能协同工作。常用于系统集成、老接口兼容新系统。

### 代码示例（Java，对象适配器）
```java
// 目标接口
public interface Target {
    void request();
}

// 需要适配的类
public class Adaptee {
    public void specificRequest() {
        System.out.println("Adaptee: 特定功能");
    }
}

// 适配器
public class Adapter implements Target {
    private Adaptee adaptee;
    public Adapter(Adaptee adaptee) {
        this.adaptee = adaptee;
    }
    @Override
    public void request() {
        // 转换接口
        adaptee.specificRequest();
    }
}

// 客户端
public class Client {
    public static void main(String[] args) {
        Target target = new Adapter(new Adaptee());
        target.request(); // 输出：Adaptee: 特定功能
    }
}
```

### 代码注释
- `Target`：客户端期望的接口。
- `Adaptee`：已有功能但接口不兼容的类。
- `Adapter`：持有 Adaptee 实例，实现 Target 接口，完成适配。
- `Client`：通过 Target 使用 Adaptee 功能。

### 注意点
- 适配器可分为类适配器（继承）和对象适配器（组合），推荐对象适配器，灵活性更高。
- 适配器只解决接口兼容，不改变原有功能。
- 适配器过多时，系统复杂度会提升。

---

## 三、行为型模式 —— 策略模式（Strategy）

### 原理
策略模式定义一系列算法，将每个算法封装起来，并使它们可以互换。客户端可在运行时选择算法，常用于多种业务规则切换。

### 代码示例（Java）
```java
// 策略接口
public interface Strategy {
    int calculate(int a, int b);
}

// 具体策略A：加法
public class AddStrategy implements Strategy {
    public int calculate(int a, int b) {
        return a + b;
    }
}

// 具体策略B：乘法
public class MultiplyStrategy implements Strategy {
    public int calculate(int a, int b) {
        return a * b;
    }
}

// 上下文
public class Context {
    private Strategy strategy;
    public Context(Strategy strategy) {
        this.strategy = strategy;
    }
    public int execute(int a, int b) {
        return strategy.calculate(a, b);
    }
}

// 客户端
public class Client {
    public static void main(String[] args) {
        Context ctx = new Context(new AddStrategy());
        System.out.println(ctx.execute(2, 3)); // 输出 5

        ctx = new Context(new MultiplyStrategy());
        System.out.println(ctx.execute(2, 3)); // 输出 6
    }
}
```

### 代码注释
- `Strategy`：算法接口，定义统一方法。
- `AddStrategy`、`MultiplyStrategy`：不同算法实现。
- `Context`：持有 Strategy，可动态切换。
- `Client`：运行时选择不同策略。

### 注意点
- 策略模式消除了 if-else/switch，便于扩展新算法。
- 策略类可复用，符合开闭原则。
- 策略数量过多时，类数量会增加，可结合工厂模式优化。

---

## 总结

- 创建型关注对象创建（如单例、工厂、建造者等）。
- 结构型关注类/对象组合（如适配器、装饰器、代理等）。
- 行为型关注对象间协作和职责分配（如策略、观察者、模板方法等）。
- 设计模式需结合实际场景选用，避免滥用导致系统复杂度提升。