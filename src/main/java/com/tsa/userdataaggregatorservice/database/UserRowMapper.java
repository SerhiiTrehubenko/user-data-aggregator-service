package com.tsa.userdataaggregatorservice.database;

import com.tsa.userdataaggregatorservice.domain.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class UserRowMapper implements RowMapper<User> {
    private final Map<String, String> mapping;

    public UserRowMapper(Map<String, String> mapping) {
        this.mapping = mapping;
    }

    @Override
    public User mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getString(mapping.get("id")))
                .username(resultSet.getString(mapping.get("username")))
                .name(resultSet.getString(mapping.get("name")))
                .surname(resultSet.getString(mapping.get("surname")))
                .build();
    }
}
