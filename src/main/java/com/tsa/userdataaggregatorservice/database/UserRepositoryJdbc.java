package com.tsa.userdataaggregatorservice.database;

import com.tsa.userdataaggregatorservice.database.configuration.DatabaseProperties;
import com.tsa.userdataaggregatorservice.database.configuration.DbContextHolder;
import com.tsa.userdataaggregatorservice.domain.User;
import com.tsa.userdataaggregatorservice.domain.UserDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Repository
public class UserRepositoryJdbc implements UserDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final List<DatabaseProperties> dbProperties;
    private final DbContextHolder dbContextHolder;
    private final UserQueryGenerator queryGenerator;

    @Override
    public List<User> findAll() {
        final ArrayList<User> users = new ArrayList<>();

        dbProperties.forEach(property -> {
            try {
                dbContextHolder.setDatabaseKey(property.getName(), property.getStrategy());
                String queryFindAll = queryGenerator.getFindAllQuery(property);
                List<User> usersBatch = jdbcTemplate.query(queryFindAll, new UserRowMapper(property.getMapping()));
                users.addAll(usersBatch);
            } catch (DataAccessException e) {
                log.error("Database round trip error. Database: {}, table: {}", property.getName(), property.getTable(), e);
            } finally {
                dbContextHolder.removeDatabaseKey();
            }
        });

        return users;
    }
}
