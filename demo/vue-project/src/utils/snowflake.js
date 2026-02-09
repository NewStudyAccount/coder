/**
 * Twitter 雪花算法 JavaScript 实现
 * 
 * ID结构: 64位数字（JavaScript中使用BigInt）
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000
 * 1位符号位 | 41位时间戳 | 5位数据中心ID | 5位机器ID | 12位序列号
 * 
 * 特性:
 * - 全局唯一
 * - 趋势递增
 * - 高性能
 * - 无依赖
 * - 线程安全（单线程环境）
 * 
 * 注意：JavaScript 的 Number 类型只能安全表示 -(2^53 - 1) 到 2^53 - 1 之间的整数
 * 因此使用 BigInt 来处理 64 位整数，在传输时需要转换为字符串
 */
class SnowflakeIdWorker {
  /**
   * 构造函数
   * @param {number} workerId - 工作机器ID (0-31)
   * @param {number} datacenterId - 数据中心ID (0-31)
   */
  constructor(workerId = 0, datacenterId = 0) {
    // ==============================常量定义==============================
    
    /** 起始时间戳 (2015-01-01 00:00:00) */
    this.twepoch = 1420041600000n;
    
    /** 机器ID所占的位数 */
    this.workerIdBits = 5n;
    
    /** 数据中心ID所占的位数 */
    this.datacenterIdBits = 5n;
    
    /** 支持的最大机器ID，结果是31 */
    this.maxWorkerId = -1n ^ (-1n << this.workerIdBits);
    
    /** 支持的最大数据中心ID，结果是31 */
    this.maxDatacenterId = -1n ^ (-1n << this.datacenterIdBits);
    
    /** 序列在ID中占的位数 */
    this.sequenceBits = 12n;
    
    /** 机器ID向左移12位 */
    this.workerIdShift = this.sequenceBits;
    
    /** 数据中心ID向左移17位(12+5) */
    this.datacenterIdShift = this.sequenceBits + this.workerIdBits;
    
    /** 时间戳向左移22位(5+5+12) */
    this.timestampLeftShift = this.sequenceBits + this.workerIdBits + this.datacenterIdBits;
    
    /** 生成序列的掩码，这里为4095 */
    this.sequenceMask = -1n ^ (-1n << this.sequenceBits);
    
    // ==============================字段定义==============================
    
    /** 工作机器ID(0~31) */
    this.workerId = BigInt(workerId);
    
    /** 数据中心ID(0~31) */
    this.datacenterId = BigInt(datacenterId);
    
    /** 毫秒内序列(0~4095) */
    this.sequence = 0n;
    
    /** 上次生成ID的时间戳 */
    this.lastTimestamp = -1n;
    
    // 验证参数
    if (this.workerId > this.maxWorkerId || this.workerId < 0n) {
      throw new Error(`worker Id 不能大于 ${this.maxWorkerId} 或小于 0`);
    }
    if (this.datacenterId > this.maxDatacenterId || this.datacenterId < 0n) {
      throw new Error(`datacenter Id 不能大于 ${this.maxDatacenterId} 或小于 0`);
    }
  }
  
  /**
   * 获得下一个ID
   * @returns {bigint} SnowflakeId
   */
  nextId() {
    let timestamp = this.timeGen();
    
    // 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过
    if (timestamp < this.lastTimestamp) {
      throw new Error(
        `时钟回退异常，拒绝生成ID。回退了 ${this.lastTimestamp - timestamp} 毫秒`
      );
    }
    
    // 如果是同一时间生成的，则进行毫秒内序列
    if (this.lastTimestamp === timestamp) {
      this.sequence = (this.sequence + 1n) & this.sequenceMask;
      // 毫秒内序列溢出
      if (this.sequence === 0n) {
        // 阻塞到下一个毫秒，获得新的时间戳
        timestamp = this.tilNextMillis(this.lastTimestamp);
      }
    } else {
      // 时间戳改变，毫秒内序列重置
      this.sequence = 0n;
    }
    
    // 上次生成ID的时间戳
    this.lastTimestamp = timestamp;
    
    // 移位并通过或运算拼到一起组成64位的ID
    return (
      ((timestamp - this.twepoch) << this.timestampLeftShift) |
      (this.datacenterId << this.datacenterIdShift) |
      (this.workerId << this.workerIdShift) |
      this.sequence
    );
  }
  
  /**
   * 生成ID并返回字符串形式（避免JavaScript精度丢失）
   * @returns {string} ID字符串
   */
  nextIdStr() {
    return this.nextId().toString();
  }
  
  /**
   * 阻塞到下一个毫秒，直到获得新的时间戳
   * @param {bigint} lastTimestamp - 上次生成ID的时间戳
   * @returns {bigint} 当前时间戳
   */
  tilNextMillis(lastTimestamp) {
    let timestamp = this.timeGen();
    while (timestamp <= lastTimestamp) {
      timestamp = this.timeGen();
    }
    return timestamp;
  }
  
  /**
   * 返回以毫秒为单位的当前时间
   * @returns {bigint} 当前时间(毫秒)
   */
  timeGen() {
    return BigInt(Date.now());
  }
  
  /**
   * 解析雪花ID
   * @param {bigint|string|number} id - 雪花ID
   * @returns {object} ID信息对象
   */
  parseId(id) {
    const bigId = typeof id === 'bigint' ? id : BigInt(id);
    
    const timestamp = Number(((bigId >> this.timestampLeftShift) & ~(-1n << 41n)) + this.twepoch);
    const datacenterId = Number((bigId >> this.datacenterIdShift) & ~(-1n << this.datacenterIdBits));
    const workerId = Number((bigId >> this.workerIdShift) & ~(-1n << this.workerIdBits));
    const sequence = Number(bigId & this.sequenceMask);
    
    return {
      id: bigId.toString(),
      timestamp,
      timestampStr: new Date(timestamp).toLocaleString('zh-CN'),
      datacenterId,
      workerId,
      sequence
    };
  }
  
  /**
   * 获取当前配置信息
   * @returns {object} 配置信息
   */
  getConfig() {
    return {
      workerId: Number(this.workerId),
      datacenterId: Number(this.datacenterId),
      maxWorkerId: Number(this.maxWorkerId),
      maxDatacenterId: Number(this.maxDatacenterId),
      maxSequence: Number(this.sequenceMask),
      description: 'Twitter Snowflake分布式ID生成器',
      idStructure: '1位符号位 + 41位时间戳 + 5位数据中心ID + 5位机器ID + 12位序列号'
    };
  }
  
  /**
   * 批量生成ID
   * @param {number} count - 生成数量
   * @returns {Array<string>} ID字符串数组
   */
  batchGenerate(count) {
    const ids = [];
    for (let i = 0; i < count; i++) {
      ids.push(this.nextIdStr());
    }
    return ids;
  }
  
  /**
   * 性能测试
   * @param {number} count - 测试数量
   * @returns {object} 性能测试结果
   */
  performanceTest(count = 10000) {
    const startTime = Date.now();
    const ids = new Set();
    let firstId = '';
    let lastId = '';
    
    for (let i = 0; i < count; i++) {
      const id = this.nextIdStr();
      if (i === 0) firstId = id;
      if (i === count - 1) lastId = id;
      ids.add(id);
    }
    
    const endTime = Date.now();
    const duration = endTime - startTime;
    const qps = duration > 0 ? Math.floor((count * 1000) / duration) : 0;
    const duplicates = count - ids.size;
    
    return {
      count,
      duration: `${duration}ms`,
      qps,
      firstId,
      lastId,
      uniqueCount: ids.size,
      duplicates
    };
  }
}

// 创建默认实例（使用默认配置）
const defaultSnowflake = new SnowflakeIdWorker(0, 0);

// 导出类和默认实例
export { SnowflakeIdWorker, defaultSnowflake };

// 提供便捷的导出函数
export default {
  /**
   * 生成一个雪花ID（字符串形式）
   * @returns {string} ID字符串
   */
  generate() {
    return defaultSnowflake.nextIdStr();
  },
  
  /**
   * 批量生成雪花ID
   * @param {number} count - 生成数量
   * @returns {Array<string>} ID字符串数组
   */
  batchGenerate(count) {
    return defaultSnowflake.batchGenerate(count);
  },
  
  /**
   * 解析雪花ID
   * @param {bigint|string|number} id - 雪花ID
   * @returns {object} ID信息对象
   */
  parse(id) {
    return defaultSnowflake.parseId(id);
  },
  
  /**
   * 获取配置信息
   * @returns {object} 配置信息
   */
  getConfig() {
    return defaultSnowflake.getConfig();
  },
  
  /**
   * 性能测试
   * @param {number} count - 测试数量
   * @returns {object} 性能测试结果
   */
  performanceTest(count) {
    return defaultSnowflake.performanceTest(count);
  },
  
  /**
   * 创建自定义实例
   * @param {number} workerId - 工作机器ID (0-31)
   * @param {number} datacenterId - 数据中心ID (0-31)
   * @returns {SnowflakeIdWorker} 雪花算法实例
   */
  createInstance(workerId, datacenterId) {
    return new SnowflakeIdWorker(workerId, datacenterId);
  }
};
