package com.tsa.userdataaggregatorservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class UserDataAggregatorServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserDataAggregatorServiceApplication.class, args);
    }

}
