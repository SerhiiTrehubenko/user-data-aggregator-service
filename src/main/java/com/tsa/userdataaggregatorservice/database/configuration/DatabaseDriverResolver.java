package com.tsa.userdataaggregatorservice.database.configuration;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class DatabaseDriverResolver {

    private Map<String, String> drivers;

    public String resolveDriver(String databaseType) {
        if (!drivers.containsKey(databaseType)) {
            throw new IllegalArgumentException("Provided unsupported database driver: " + databaseType);
        }
        return drivers.get(databaseType);
    }
}
