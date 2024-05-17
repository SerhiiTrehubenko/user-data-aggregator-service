package com.tsa.userdataaggregatorservice.database;

import com.tsa.userdataaggregatorservice.database.configuration.DatabaseProperties;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserQueryGenerator {

    private static final String FIND_ALL_QUERY = "SELECT %s, %s, %s, %s FROM %s;";

    public String getFindAllQuery(DatabaseProperties properties) {
        String table = properties.getTable();
        Map<String, String> mapping = properties.getMapping();
        String id = mapping.get("id");
        String username = mapping.get("username");
        String name = mapping.get("name");
        String surname = mapping.get("surname");

        return FIND_ALL_QUERY.formatted(id, username, name, surname, table);
    }

}
