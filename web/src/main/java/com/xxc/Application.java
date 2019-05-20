package com.xxc;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author: xixincan
 * @Date: 2019/5/19
 * @Version 1.0
 */
@SpringBootApplication
@MapperScan("com.xxc.dao.mapper")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
