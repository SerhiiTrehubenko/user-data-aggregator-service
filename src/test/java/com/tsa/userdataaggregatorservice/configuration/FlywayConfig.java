package com.tsa.userdataaggregatorservice.configuration;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
@Slf4j
@Profile("test")
public class FlywayConfig {
    public static final String FLYWAY_MIGRATIONS_LOCATION = "db/%s";

    private final Map<String, DataSource> databasesHolder;

    public FlywayConfig(@Qualifier("dataSources") Map<String, DataSource> databasesHolder) {
        this.databasesHolder = databasesHolder;
    }

    @PostConstruct
    public void migrateFlyway() {
        databasesHolder.forEach((key, value) -> {
            log.info("FLYWAY FOR DB {}", key);
            FluentConfiguration config =
                    Flyway.configure()
                            .dataSource(value)
                            .locations(FLYWAY_MIGRATIONS_LOCATION.formatted(key));
            Flyway flyway = new Flyway(config);
            flyway.migrate();
        });
    }
}
