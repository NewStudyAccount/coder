package com.example.demo.patterns;

import java.util.Properties;

/**
 * 【创建型-单例模式实际案例】
 * 场景：全局配置管理器，保证配置只加载一次，所有模块共享
 */
public class SingletonExample {

    /**
     * 配置管理器（饿汉式单例）
     */
    public static class ConfigManager {
        // 唯一实例
        private static final ConfigManager INSTANCE = new ConfigManager();

        private Properties props = new Properties();

        // 私有构造，防止外部new
        private ConfigManager() {
            // 模拟加载配置
            props.setProperty("db.url", "jdbc:mysql://localhost:3306/demo");
            props.setProperty("db.user", "root");
            props.setProperty("db.pwd", "123456");
        }

        // 全局访问点
        public static ConfigManager getInstance() {
            return INSTANCE;
        }

        // 获取配置
        public String getProperty(String key) {
            return props.getProperty(key);
        }
    }

    /**
     * 实际业务调用
     */
    public static void main(String[] args) {
        // 获取全局唯一配置管理器
        ConfigManager config = ConfigManager.getInstance();

        // 读取配置
        String url = config.getProperty("db.url");
        String user = config.getProperty("db.user");
        String pwd = config.getProperty("db.pwd");

        System.out.println("数据库连接：" + url);
        System.out.println("用户名：" + user);
        System.out.println("密码：" + pwd);

        // 再次获取实例，验证单例
        ConfigManager config2 = ConfigManager.getInstance();
        System.out.println("同一个实例？" + (config == config2)); // true
    }
}

/*
【详细解释】
1. ConfigManager 构造方法私有，外部无法 new，只能通过 getInstance() 获取唯一实例。
2. props 属性只加载一次，所有模块共享，节省资源。
3. main 方法模拟业务场景：读取数据库配置，保证全局唯一。
4. 单例适合无状态或只读状态的场景，如配置、日志、线程池等。
5. 若需懒加载或多线程安全，需用双重检查锁定等写法。
*/