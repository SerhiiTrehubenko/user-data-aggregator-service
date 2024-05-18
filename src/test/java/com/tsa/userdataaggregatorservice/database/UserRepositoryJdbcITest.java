package com.tsa.userdataaggregatorservice.database;

import com.tsa.userdataaggregatorservice.domain.User;
import com.tsa.userdataaggregatorservice.domain.UserDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserRepositoryJdbcITest
        extends AbstractContainersJdbcTest {

    @Autowired
    UserDao userDao;

    @Test
    void shouldFindAllUsers() {
        String expectedUsers = "[User(id=example-user-id-1, username=login-1, name=first-name-1, surname=last_name-1), User(id=example-user-id-2, username=login-2, name=first_name-2, surname=last_name-2), User(id=example-user-id-3, username=login-3, name=first-name-3, surname=last_name-3), User(id=example-user-id-4, username=login-4, name=first_name-4, surname=last_name-4)]";
        final List<User> users = userDao.findAll();

        assertNotNull(users);
        assertEquals(expectedUsers, users.toString());
    }

    @Test
    void shouldFindUsersWithEqualsOperatorFilter() {
        String expectedUsers = "[User(id=example-user-id-1, username=login-1, name=first-name-1, surname=last_name-1)]";
        String filterClause = "id=example-user-id-1";

        List<User> users = userDao.findAllFilter(filterClause);

        assertNotNull(users);
        assertEquals(expectedUsers, users.toString());
    }

    @Test
    void shouldFindAllUsersWithOperatorORFilter() {
        String expectedUsers = "[User(id=example-user-id-1, username=login-1, name=first-name-1, surname=last_name-1), User(id=example-user-id-3, username=login-3, name=first-name-3, surname=last_name-3)]";
        String filterClause = "id=example-user-id-1,OR,id=example-user-id-3";

        List<User> users = userDao.findAllFilter(filterClause);

        assertNotNull(users);
        assertEquals(expectedUsers, users.toString());
    }

    @Test
    void shouldFindAllUsersWithOperatorANDFilter() {
        String expectedUsers = "[User(id=example-user-id-1, username=login-1, name=first-name-1, surname=last_name-1), User(id=example-user-id-2, username=login-2, name=first_name-2, surname=last_name-2), User(id=example-user-id-3, username=login-3, name=first-name-3, surname=last_name-3)]";
        String filterClause = "id>=example-user-id-1,AND,username<=login-3";

        List<User> users = userDao.findAllFilter(filterClause);

        assertNotNull(users);
        assertEquals(expectedUsers, users.toString());
    }

    @Test
    void shouldFindAllUsersWithOperatorLIKEFilter() {
        String expectedUsers = "[User(id=example-user-id-1, username=login-1, name=first-name-1, surname=last_name-1), User(id=example-user-id-3, username=login-3, name=first-name-3, surname=last_name-3)]";
        String filterClause = "id LIKE %user-id-1,OR,id LIKE %user-id-3";

        List<User> users = userDao.findAllFilter(filterClause);

        assertNotNull(users);
        assertEquals(expectedUsers, users.toString());
    }
}
