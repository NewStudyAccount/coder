package com.example.demo.patterns;

/**
 * 【结构型-适配器模式实际案例】
 * 场景：老版日志系统适配新日志接口（如将自定义日志适配为 SLF4J Logger）
 */
public class AdapterExample {

    // 新日志接口（目标接口，假设为第三方库）
    public interface Logger {
        void info(String msg);
        void error(String msg);
    }

    // 老版日志系统（需要适配的类）
    public static class OldLogger {
        public void print(String level, String msg) {
            System.out.println("[" + level + "] " + msg);
        }
    }

    // 适配器：让 OldLogger 支持 Logger 接口
    public static class LoggerAdapter implements Logger {
        private OldLogger oldLogger;

        public LoggerAdapter(OldLogger oldLogger) {
            this.oldLogger = oldLogger;
        }

        @Override
        public void info(String msg) {
            oldLogger.print("INFO", msg);
        }

        @Override
        public void error(String msg) {
            oldLogger.print("ERROR", msg);
        }
    }

    /**
     * 实际业务调用
     */
    public static void main(String[] args) {
        // 老系统日志
        OldLogger oldLogger = new OldLogger();

        // 新系统期望 Logger 接口，使用适配器
        Logger logger = new LoggerAdapter(oldLogger);

        // 业务代码只依赖 Logger 接口
        logger.info("系统启动成功");
        logger.error("发生异常");
    }
}

/*
【详细解释】
1. Logger 是新系统/三方库要求的日志接口，info/error 方法。
2. OldLogger 是老系统日志实现，只有 print(level, msg) 方法。
3. LoggerAdapter 实现 Logger，内部持有 OldLogger，将 info/error 转换为 print。
4. main 方法模拟业务场景：新业务只依赖 Logger，适配器无缝兼容老日志。
5. 适配器模式常用于系统集成、老接口兼容新系统，推荐对象适配器（组合）。
*/