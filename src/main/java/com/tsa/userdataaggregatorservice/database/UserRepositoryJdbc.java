package com.tsa.userdataaggregatorservice.database;

import com.tsa.userdataaggregatorservice.database.configuration.DatabaseProperties;
import com.tsa.userdataaggregatorservice.exception.FetchDataException;
import com.tsa.userdataaggregatorservice.domain.User;
import com.tsa.userdataaggregatorservice.domain.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Repository
public class UserRepositoryJdbc implements UserDao {
    private static final String EMPTY_FILTER = "";
    private final List<DatabaseProperties> dbProperties;
    private final UserQueryGenerator queryGenerator;
    private final Map<String, NamedParameterJdbcTemplate> jdbcTemplates;

    public UserRepositoryJdbc(List<DatabaseProperties> dbProperties,
                              UserQueryGenerator queryGenerator,
                              @Qualifier("mapTemplates") Map<String, NamedParameterJdbcTemplate> jdbcTemplates) {
        this.dbProperties = dbProperties;
        this.queryGenerator = queryGenerator;
        this.jdbcTemplates = jdbcTemplates;
    }

    @Override
    public List<User> findAll() {
        return findAllFilter(EMPTY_FILTER);
    }

    @Override
    public List<User> findAllFilter(String filter) {
        return Flux
                .fromStream(dbProperties.stream())
                .flatMapSequential(properties -> task(properties, filter))
                .flatMap(Flux::fromIterable)
                .collectList()
                .block();
    }

    private Mono<List<User>> task(DatabaseProperties properties, String filter) {
        return Mono.just(properties)
                .map(databaseProperties -> {
                    QueryHolder queryHolder = queryGenerator.getFindAllQueryWithFilter(databaseProperties, filter);
                    try {
                        return getUsers(databaseProperties, queryHolder);
                    } catch (Exception exception) {
                        logError(databaseProperties, queryHolder, exception);
                        throw fetchDataException(databaseProperties, queryHolder, exception);
                    }
                });
    }

    private List<User> getUsers(DatabaseProperties databaseProperties, QueryHolder queryHolder) {
        var currentTemplate = jdbcTemplates.get(databaseProperties.getName());
        if (Objects.nonNull(queryHolder.getParamMap())) {
            return currentTemplate.query(queryHolder.getQuery(), queryHolder.getParamMap(), new UserRowMapper(databaseProperties.getMapping()));
        }
        return currentTemplate.query(queryHolder.getQuery(), new UserRowMapper(databaseProperties.getMapping()));
    }

    private void logError(DatabaseProperties databaseProperties, QueryHolder queryHolder, Exception exception) {
        log.error("Database round trip error. Database: {}, table: {}, query:{}",
                databaseProperties.getName(), databaseProperties.getTable(), queryHolder.getQuery(), exception);
    }

    private FetchDataException fetchDataException(DatabaseProperties databaseProperties, QueryHolder queryHolder, Exception exception) {
        throw new FetchDataException(
                "Database round trip error. Database: %s, table: %s, query:%s".formatted(
                        databaseProperties.getName(), databaseProperties.getTable(), queryHolder.getQuery()
                ),
                exception
        );
    }
}
