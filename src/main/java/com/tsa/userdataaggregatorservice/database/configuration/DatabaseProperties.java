package com.tsa.userdataaggregatorservice.database.configuration;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class DatabaseProperties {
    private String name;
    private String strategy;
    private String url;
    private String table;
    private String user;
    private String password;
    private Map<String, String> mapping;
}
