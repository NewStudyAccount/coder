package com.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.admin.mapper")
public class AdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
        System.out.println("========================================");
        System.out.println("后台管理系统启动成功！");
        System.out.println("访问地址：http://localhost:8080/api");
        System.out.println("========================================");
    }
}
