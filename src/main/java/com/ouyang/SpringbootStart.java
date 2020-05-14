package com.ouyang;

import com.ouyang.spring.SpringUntils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@ComponentScan(basePackages = {"com.ouyang.**"})
@MapperScan("com.ouyang.dao")
@EnableScheduling  // 开启定时任务
@EnableTransactionManagement// 开启事务
@ConfigurationProperties(prefix="spring.datasource")
public class SpringbootStart {

    @Bean
    public SpringUntils springUtil2(){return new SpringUntils();}

    public static void main(String[] args) {
        SpringApplication.run(SpringbootStart.class, args);
    }
}
