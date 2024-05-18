package com.tsa.userdataaggregatorservice.database;

import com.tsa.userdataaggregatorservice.database.configuration.DatabaseProperties;
import com.tsa.userdataaggregatorservice.database.configuration.DbContextHolder;
import com.tsa.userdataaggregatorservice.domain.User;
import com.tsa.userdataaggregatorservice.domain.UserDao;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserRepositoryJdbcTest {

    @Test
    void shouldExecuteFindAllUsersLogic() {
        String nameDb = "db-1";
        String strategy = "postgres";
        String findAllQuery = "SELECT * FROM users;";
        final String emptyFilter = "";
        QueryHolder queryHolder = new QueryHolder();
        queryHolder.setQuery(findAllQuery);

        List<User> expectedUsers = List.of(User.builder().id("1").name("Serhii").build());

        DatabaseProperties propertiesDb = mock(DatabaseProperties.class);
        when(propertiesDb.getName()).thenReturn(nameDb);
        when(propertiesDb.getStrategy()).thenReturn(strategy);

        List<DatabaseProperties> allProperties = List.of(propertiesDb);

        DbContextHolder dbContextHolder = mock(DbContextHolder.class);
        when(dbContextHolder.getDatabaseKey()).thenReturn(nameDb);

        UserQueryGenerator queryGenerator = mock(UserQueryGenerator.class);
        when(queryGenerator.getFindAllQuery(propertiesDb)).thenReturn(findAllQuery);
        when(queryGenerator.getFindAllQueryWithFilter(propertiesDb, emptyFilter)).thenReturn(queryHolder);

        NamedParameterJdbcTemplate jdbcTemplate = mock(NamedParameterJdbcTemplate.class);
        when(jdbcTemplate.query(eq(findAllQuery), any(UserRowMapper.class))).thenReturn(expectedUsers);

        UserDao userRepoSut = new UserRepositoryJdbc(jdbcTemplate, allProperties, dbContextHolder, queryGenerator);

        List<User> users = userRepoSut.findAll();
        assertNotNull(users);
        assertEquals(1, users.size());

        verify(propertiesDb).getName();
        verify(propertiesDb).getStrategy();
        verify(dbContextHolder).setDatabaseKey(nameDb, strategy);
        verify(queryGenerator).getFindAllQueryWithFilter(eq(propertiesDb), anyString());
        verify(jdbcTemplate, only()).query(eq(findAllQuery), any(UserRowMapper.class));
        verify(dbContextHolder).removeDatabaseKey();
    }
}
