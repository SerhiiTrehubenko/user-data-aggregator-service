package com.tsa.userdataaggregatorservice.database;

import com.tsa.userdataaggregatorservice.database.configuration.DatabaseProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserQueryGenerator {

    private static final String RAW_LIKE = "LIKE";
    private static final String NORM_LIKE = " LIKE ";
    private static final Set<String> LOGIC_OPERATORS = Set.of("AND", "OR", RAW_LIKE);
    private static final String FIND_ALL_QUERY = "SELECT %s, %s, %s, %s FROM %s;";
    private static final String PLACE_HOLDER = " %s";
    private static final String DEFAULT_DELIMITER = ",";
    private static final String SEMICOLON = ";";
    private static final String QUERY_WITH_CLAUSE = "%s WHERE%s;";
    private static final String BLANK_STRING = "";
    private static final String OPERATORS = "!=|=|<=|>=|>|<|LIKE";
    private static final String BINDER = "%s%s%s";
    private static final int KEY = 0;
    private static final int VALUE = 1;
    private static final Pattern pattern = Pattern.compile(OPERATORS);

    @Value("${clause-delimiter:,}")
    private String delimiter;


    public String getFindAllQuery(DatabaseProperties properties) {
        String table = properties.getTable();
        Map<String, String> mapping = properties.getMapping();
        String id = mapping.get("id");
        String username = mapping.get("username");
        String name = mapping.get("name");
        String surname = mapping.get("surname");

        return FIND_ALL_QUERY.formatted(id, username, name, surname, table);
    }

    public QueryHolder getFindAllQueryWithFilter(DatabaseProperties properties, String filterClause) {
        if (Objects.isNull(filterClause) || filterClause.isBlank()) {
            final QueryHolder holder = new QueryHolder();
            holder.setQuery(getFindAllQuery(properties));
            return holder;
        }
        Map<String, String> mapping = properties.getMapping();

        String findAllQuery = getFindAllQuery(properties).replace(SEMICOLON, BLANK_STRING);

        QueryHolder queryHolder = getQueryHolderWithClause(filterClause, mapping);

        queryHolder.setQuery(QUERY_WITH_CLAUSE.formatted(findAllQuery, queryHolder.getClause()));
        return queryHolder;
    }

    QueryHolder getQueryHolderWithClause(String rawParameters, Map<String, String> mapping) {
        String resolvedDelimiter = Objects.isNull(delimiter) ? DEFAULT_DELIMITER : delimiter;

        AtomicReference<Parameter> previousParameter = new AtomicReference<>();
        previousParameter.set(Parameter.builder().parameter(BLANK_STRING).build());

        QueryHolder holder = new QueryHolder();
        AtomicInteger placeholderIncrement = new AtomicInteger();
        Map<String, String> paramMap = new HashMap<>();

        String[] splitRawParameters = rawParameters.split(resolvedDelimiter);

        List<String> normalizedParameters = Arrays.stream(splitRawParameters)
                .map(param -> {
                    Parameter currentPair = normalizeParameter(param, mapping);

                    if (!previousParameter.get().getParameter().equalsIgnoreCase(currentPair.getParameter())) {
                        previousParameter.set(currentPair);

                        placeholderIncrement.addAndGet(currentPair.putToParamMap(paramMap, placeholderIncrement.get()));
                        return currentPair.getParameter();
                    }
                    return BLANK_STRING;
                })
                .filter(param -> !param.isBlank())
                .toList();

        StringJoiner joiner = new StringJoiner(BLANK_STRING);
        normalizedParameters.forEach(param -> joiner.add(PLACE_HOLDER.formatted(param)));
        holder.setClause(joiner.toString());
        holder.setParamMap(paramMap);
        return holder;
    }

    private Parameter normalizeParameter(String rawParameter, Map<String, String> mapping) {
        if (LOGIC_OPERATORS.contains(rawParameter)) {
            return Parameter.builder().parameter(rawParameter).build();
        }

        String operator = resolveOperator(rawParameter);

        String[] splitParameter = rawParameter.split(OPERATORS);
        if (splitParameter.length != 2) {
            return Parameter.builder().parameter(BLANK_STRING).build();
        }

        String parameterKey = splitParameter[KEY].trim();
        String normalizedParameterKey = mapping.get(parameterKey);
        if (Objects.isNull(normalizedParameterKey)) {
            return Parameter.builder().parameter(BLANK_STRING).build();
        }
        String namedPlaceholder = ":%s".formatted(normalizedParameterKey);
        String finalParam = BINDER.formatted(normalizedParameterKey, operator, namedPlaceholder);

        return Parameter.builder()
                .parameter(finalParam)
                .key(normalizedParameterKey)
                .value(splitParameter[VALUE].trim())
                .build();
    }

    private String resolveOperator(String rawParameter) {
        Matcher matcher = pattern.matcher(rawParameter);
        String operatorHolder = BLANK_STRING;
        while (matcher.find()) {
            operatorHolder = matcher.group();
        }
        operatorHolder =
                LOGIC_OPERATORS.contains(operatorHolder) && operatorHolder.equals(RAW_LIKE)
                        ? NORM_LIKE
                        : operatorHolder;

        return operatorHolder;
    }
}
