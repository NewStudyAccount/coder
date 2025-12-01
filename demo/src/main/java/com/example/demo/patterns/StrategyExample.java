package com.example.demo.patterns;

/**
 * 【行为型-策略模式实际案例】
 * 场景：订单价格计算支持多种优惠策略（如满减、打折、无优惠）
 */
public class StrategyExample {

    // 策略接口：定义统一的价格计算方法
    public interface DiscountStrategy {
        double calcPrice(double originalPrice);
    }

    // 具体策略A：无优惠
    public static class NoDiscount implements DiscountStrategy {
        public double calcPrice(double originalPrice) {
            return originalPrice;
        }
    }

    // 具体策略B：满100减20
    public static class FullReduction implements DiscountStrategy {
        public double calcPrice(double originalPrice) {
            if (originalPrice >= 100) {
                return originalPrice - 20;
            }
            return originalPrice;
        }
    }

    // 具体策略C：打8折
    public static class PercentageDiscount implements DiscountStrategy {
        public double calcPrice(double originalPrice) {
            return originalPrice * 0.8;
        }
    }

    // 上下文：订单
    public static class Order {
        private double originalPrice;
        private DiscountStrategy discountStrategy;

        public Order(double originalPrice, DiscountStrategy discountStrategy) {
            this.originalPrice = originalPrice;
            this.discountStrategy = discountStrategy;
        }

        // 计算最终价格
        public double getFinalPrice() {
            return discountStrategy.calcPrice(originalPrice);
        }
    }

    /**
     * 实际业务调用
     */
    public static void main(String[] args) {
        double price = 120;

        // 1. 无优惠
        Order order1 = new Order(price, new NoDiscount());
        System.out.println("无优惠：" + order1.getFinalPrice());

        // 2. 满减
        Order order2 = new Order(price, new FullReduction());
        System.out.println("满100减20：" + order2.getFinalPrice());

        // 3. 打折
        Order order3 = new Order(price, new PercentageDiscount());
        System.out.println("打8折：" + order3.getFinalPrice());
    }
}

/*
【详细解释】
1. DiscountStrategy 定义统一的价格计算方法，便于扩展新优惠。
2. NoDiscount/FullReduction/PercentageDiscount 分别实现不同优惠规则。
3. Order 作为上下文，持有 DiscountStrategy，可灵活切换。
4. main 方法模拟业务场景：同一订单金额，选择不同优惠策略，得到不同价格。
5. 策略模式消除 if-else，便于扩展和维护，符合开闭原则。
*/