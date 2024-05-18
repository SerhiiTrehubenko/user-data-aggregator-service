package com.tsa.userdataaggregatorservice.database;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class QueryHolder {
    private String query;
    private String clause;
    private Map<String, String> paramMap;
}
