package com.classcheck;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 课堂学情监控管理系统 启动类
 */
@SpringBootApplication
@MapperScan("com.classcheck.mapper")
@EnableScheduling
public class ClasscheckApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClasscheckApplication.class, args);
    }
}
