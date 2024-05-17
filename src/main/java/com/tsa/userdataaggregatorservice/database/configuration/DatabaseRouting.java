package com.tsa.userdataaggregatorservice.database.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

@RequiredArgsConstructor
public class DatabaseRouting extends AbstractRoutingDataSource {
    private final DbContextHolder dbContextHolder;

    @Override
    protected Object determineCurrentLookupKey() {
        return dbContextHolder.getDatabaseKey();
    }
}
