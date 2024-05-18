package com.tsa.userdataaggregatorservice.database;

import com.tsa.userdataaggregatorservice.database.configuration.DatabaseProperties;
import com.tsa.userdataaggregatorservice.database.configuration.DbContextHolder;
import com.tsa.userdataaggregatorservice.exception.FetchDataException;
import com.tsa.userdataaggregatorservice.domain.User;
import com.tsa.userdataaggregatorservice.domain.UserDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

@RequiredArgsConstructor
@Slf4j
@Repository
public class UserRepositoryJdbc implements UserDao {
    private static final String EMPTY_FILTER = "";
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final List<DatabaseProperties> dbProperties;
    private final DbContextHolder dbContextHolder;
    private final UserQueryGenerator queryGenerator;

    @Override
    public List<User> findAll() {
        return findAllFilter(EMPTY_FILTER);
    }

    @Override
    public List<User> findAllFilter(String filter) {
        return dbProperties.stream()
                .map(prop -> findGenericAllUser(
                        prop,
                        () -> queryGenerator.getFindAllQueryWithFilter(prop, filter))
                )
                .flatMap(List::stream)
                .toList();
    }

    private List<User> findGenericAllUser(DatabaseProperties prop, Supplier<QueryHolder> generator) {

        QueryHolder queryHolder = generator.get();
        try {
            dbContextHolder.setDatabaseKey(prop.getName(), prop.getStrategy());
            if (Objects.nonNull(queryHolder.getParamMap())) {
                return jdbcTemplate.query(queryHolder.getQuery(), queryHolder.getParamMap(), new UserRowMapper(prop.getMapping()));
            }
            return jdbcTemplate.query(queryHolder.getQuery(), new UserRowMapper(prop.getMapping()));
        } catch (Exception exception) {
            log.error("Database round trip error. Database: {}, table: {}, query:{}",
                    prop.getName(), prop.getTable(), queryHolder.getQuery(), exception);
            throw new FetchDataException(
                    "Database round trip error. Database: %s, table: %s, query:%s".formatted(
                            prop.getName(), prop.getTable(), queryHolder.getQuery()
                    ),
                    exception
            );
        } finally {
            dbContextHolder.removeDatabaseKey();
        }
    }
}
