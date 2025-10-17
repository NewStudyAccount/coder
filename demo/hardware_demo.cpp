#include <iostream>
#include <fstream>
#include <ctime>
#include <string>
#ifdef _WIN32
#include <windows.h>
#else
#include <sys/statvfs.h>
#endif

// 简单日志系统
class Logger {
public:
    enum Level { INFO, WARNING, ERROR };

    Logger(const std::string& filename) : logFile(filename, std::ios::app) {}

    void log(Level level, const std::string& msg) {
        std::string levelStr;
        switch (level) {
            case INFO: levelStr = "INFO"; break;
            case WARNING: levelStr = "WARNING"; break;
            case ERROR: levelStr = "ERROR"; break;
        }
        std::string timeStr = getTime();
        std::string logMsg = "[" + timeStr + "][" + levelStr + "] " + msg;
        std::cout << logMsg << std::endl;
        if (logFile.is_open()) {
            logFile << logMsg << std::endl;
        }
    }

private:
    std::ofstream logFile;

    std::string getTime() {
        std::time_t now = std::time(nullptr);
        char buf[32];
        std::strftime(buf, sizeof(buf), "%Y-%m-%d %H:%M:%S", std::localtime(&now));
        return buf;
    }
};

// 枚举磁盘空间（跨平台示例）
void printDiskSpace(Logger& logger) {
#ifdef _WIN32
    ULARGE_INTEGER freeBytesAvailable, totalNumberOfBytes, totalNumberOfFreeBytes;
    if (GetDiskFreeSpaceExA("C:\\", &freeBytesAvailable, &totalNumberOfBytes, &totalNumberOfFreeBytes)) {
        logger.log(Logger::INFO, "C盘总空间: " + std::to_string(totalNumberOfBytes.QuadPart / (1024 * 1024)) + " MB");
        logger.log(Logger::INFO, "C盘可用空间: " + std::to_string(totalNumberOfFreeBytes.QuadPart / (1024 * 1024)) + " MB");
    } else {
        logger.log(Logger::ERROR, "无法获取磁盘空间信息");
    }
#else
    struct statvfs stat;
    if (statvfs("/", &stat) == 0) {
        logger.log(Logger::INFO, "根目录总空间: " + std::to_string((stat.f_blocks * stat.f_frsize) / (1024 * 1024)) + " MB");
        logger.log(Logger::INFO, "根目录可用空间: " + std::to_string((stat.bavail * stat.frsize) / (1024 * 1024)) + " MB");
    } else {
        logger.log(Logger::ERROR, "无法获取磁盘空间信息");
    }
#endif
}

// 简单串口通信（伪代码，实际需根据平台实现）
void serialPortDemo(Logger& logger) {
    logger.log(Logger::INFO, "打开串口 COM1...");
    // Windows: CreateFileA("COM1", ...)
    // Linux: open("/dev/ttyS0", ...)
    logger.log(Logger::INFO, "发送数据到串口...");
    // write(serial_fd, buffer, len);
    logger.log(Logger::INFO, "关闭串口...");
    // close(serial_fd);
}

// 文件资源处理
void fileResourceDemo(Logger& logger) {
    std::ofstream ofs("test.txt");
    if (ofs.is_open()) {
        ofs << "硬件资源处理示例：写入文件" << std::endl;
        ofs.close();
        logger.log(Logger::INFO, "文件写入成功");
    } else {
        logger.log(Logger::ERROR, "无法打开文件");
    }
}

int main() {
    Logger logger("log.txt");
    logger.log(Logger::INFO, "硬件资源处理C++示例启动");
    printDiskSpace(logger);
    serialPortDemo(logger);
    fileResourceDemo(logger);
    logger.log(Logger::INFO, "硬件资源处理C++示例结束");
    return 0;
}