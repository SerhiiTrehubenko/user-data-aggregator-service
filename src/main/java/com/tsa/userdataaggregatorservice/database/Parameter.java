package com.tsa.userdataaggregatorservice.database;

import lombok.Builder;

import java.util.Map;
import java.util.Objects;

@Builder
public class Parameter {
    private String parameter;
    private String key;
    private String value;

    public int putToParamMap(Map<String, String> paramMap, int placeholderIncrement) {
        if (check(key) && check(value) && paramMap.containsKey(key)) {
            paramMap.put(key + placeholderIncrement, value);
            parameter = parameter + placeholderIncrement;
            return placeholderIncrement + 1;
        } else {
            paramMap.put(key, value);
            return placeholderIncrement;
        }
    }

    private boolean check(String underCheck) {
        return Objects.nonNull(underCheck) && !underCheck.isBlank();
    }

    public String getParameter() {
        return parameter;
    }
}
