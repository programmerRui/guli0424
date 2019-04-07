package com.neusoft.mangerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(basePackages = "com.neusoft.mangerservice.dao")
public class MangerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MangerServiceApplication.class, args);
    }

}
