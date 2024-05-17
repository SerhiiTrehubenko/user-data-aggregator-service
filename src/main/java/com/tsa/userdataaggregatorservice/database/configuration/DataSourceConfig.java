package com.tsa.userdataaggregatorservice.database.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    @Bean
    public DbContextHolder dbContextHolder() {
        return new DbContextHolder();
    }

    @Bean
    public DataSource dataSource() {
        DatabaseRouting routing = new DatabaseRouting(dbContextHolder());
        final Map<Object, Object> holderFixture = this.databasesHolder()
                .entrySet()
                .stream()
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));

        routing.setDefaultTargetDataSource(
                holderFixture.values().stream()
                        .filter(Objects::nonNull)
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Has not been found any DataSource")));
        routing.setTargetDataSources(holderFixture);

        return routing;
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
