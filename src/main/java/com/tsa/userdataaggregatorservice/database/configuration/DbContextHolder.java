package com.tsa.userdataaggregatorservice.database.configuration;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class DbContextHolder {
    private static final ThreadLocal<String> databaseKey = new ThreadLocal<>();

    public void setDatabaseKey(String newKey, String strategy) {
        Objects.requireNonNull(newKey, "Database key cannot be NULL");
        log.info("Database key: [{} > {}] has been SET to database context", newKey, strategy);
        databaseKey.set(newKey);
    }

    public String getDatabaseKey() {
        return databaseKey.get();
    }

    public void removeDatabaseKey() {
        String currentKey = databaseKey.get();
        log.info("Database key: [{}] has been REMOVED from database context", currentKey);
        databaseKey.remove();
    }
}
