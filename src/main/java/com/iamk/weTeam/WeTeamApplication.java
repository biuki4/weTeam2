package com.iamk.weTeam;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
// @MapperScan("com.iamk.weTeam.mapper")
@EnableTransactionManagement
@EnableCaching
// 开启异步调用
@EnableAsync
public class WeTeamApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeTeamApplication.class, args);
    }

}
