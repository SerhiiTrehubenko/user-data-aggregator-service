package com.tsa.userdataaggregatorservice.database.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.*;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class DataSourceConfig {
    @Value("${db-connection.pool.size:5}")
    private int poolSizeMax;

    @Bean
    @ConfigurationProperties(prefix = "database-drivers")
    public DatabaseDriverResolver driverResolver() {
        return new DatabaseDriverResolver();
    }

    @Bean
    @ConfigurationProperties(prefix = "data-sources")
    public List<DatabaseProperties> databaseProperties() {
        return new ArrayList<>();
    }

    @Bean("mapTemplates")
    @Primary
    public Map<String, NamedParameterJdbcTemplate> jdbcTemplates() {
        return databasesHolder()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> new NamedParameterJdbcTemplate(entry.getValue())));
    }

    @Bean("dataSources")
    public Map<String, DataSource> databasesHolder() {
        List<DatabaseProperties> properties = databaseProperties();
        return properties.stream()
                .collect(Collectors.toMap(DatabaseProperties::getName, this::createDataSource));
    }

    private DataSource createDataSource(DatabaseProperties properties) {
        HikariConfig poolConfig = new HikariConfig();
        poolConfig.setPoolName("database-pool-%s".formatted(properties.getName()));
        poolConfig.setDriverClassName(driverResolver().resolveDriver(properties.getStrategy()));
        poolConfig.setJdbcUrl(properties.getUrl());
        poolConfig.setUsername(properties.getUser());
        poolConfig.setPassword(properties.getPassword());
        poolConfig.setAutoCommit(true);
        poolConfig.setMaximumPoolSize(poolSizeMax);
        return new HikariDataSource(poolConfig);
    }
}
