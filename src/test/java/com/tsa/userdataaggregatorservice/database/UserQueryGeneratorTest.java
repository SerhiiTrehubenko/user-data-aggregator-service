package com.tsa.userdataaggregatorservice.database;

import com.tsa.userdataaggregatorservice.database.configuration.DatabaseProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserQueryGeneratorTest {

    String commonQueryPart = "SELECT user_id, login, first_name, last_name FROM users WHERE";
    Map<String, String> mapping = Map.of(
            "id", "user_id",
            "username", "login",
            "name", "first_name",
            "surname", "last_name"
    );

    DatabaseProperties properties;
    UserQueryGenerator generatorSut = new UserQueryGenerator();

    @BeforeEach
    void setUp() {
        properties = mock(DatabaseProperties.class);
        when(properties.getMapping()).thenReturn(mapping);
        when(properties.getTable()).thenReturn("users");
    }

    @Test
    void shouldGenerateWHEREClauseWithOneParameter() {
        String filter = "id=10";

        String expectedQuery = commonQueryPart + " user_id=:user_id;";

        QueryHolder filteredQuery = generatorSut.getFindAllQueryWithFilter(properties, filter);
        assertEquals(expectedQuery, filteredQuery.getQuery());
        assertEquals("10", filteredQuery.getParamMap().get("user_id"));

    }

    @Test
    void shouldGenerateWHEREClauseWithTwoParametersWithLogicalOperator() {
        String filter = "id=10,OR,id=20";

        String expectedQuery = commonQueryPart + " user_id=:user_id OR user_id=:user_id0;";

        QueryHolder filteredQuery = generatorSut.getFindAllQueryWithFilter(properties, filter);
        assertEquals(expectedQuery, filteredQuery.getQuery());
        assertEquals("10", filteredQuery.getParamMap().get("user_id"));
        assertEquals("20", filteredQuery.getParamMap().get("user_id0"));
    }

    @Test
    void shouldGenerateWHEREClauseWithThreeParametersWithLogicalOperator() {
        String filter = "id=10,OR,id=20,AND,name=serhii";

        String expectedQuery = commonQueryPart + " user_id=:user_id OR user_id=:user_id0 AND first_name=:first_name;";

        QueryHolder filteredQuery = generatorSut.getFindAllQueryWithFilter(properties, filter);
        assertEquals(expectedQuery, filteredQuery.getQuery());
        assertEquals("10", filteredQuery.getParamMap().get("user_id"));
        assertEquals("20", filteredQuery.getParamMap().get("user_id0"));
        assertEquals("serhii", filteredQuery.getParamMap().get("first_name"));
    }

    @Test
    void shouldGenerateWHEREClauseWithParameterThatDoesNotExistWithLogicalOperator() {
        String filter = "id=10,OR,id=20,AND,name=serhii,papapa=popo";

        String expectedQuery = commonQueryPart + " user_id=:user_id OR user_id=:user_id0 AND first_name=:first_name;";

        QueryHolder filteredQuery = generatorSut.getFindAllQueryWithFilter(properties, filter);
        assertEquals(expectedQuery, filteredQuery.getQuery());
        assertEquals("10", filteredQuery.getParamMap().get("user_id"));
        assertEquals("20", filteredQuery.getParamMap().get("user_id0"));
        assertEquals("serhii", filteredQuery.getParamMap().get("first_name"));
    }

    @Test
    void shouldGenerateWHEREClauseWithLogicOperatorThatDoesNotExist() {
        String filter = "id=10,OR,id=20,AND,ZZZ,name=serhii,papapa=popo";

        String expectedQuery = commonQueryPart + " user_id=:user_id OR user_id=:user_id0 AND first_name=:first_name;";

        QueryHolder filteredQuery = generatorSut.getFindAllQueryWithFilter(properties, filter);
        assertEquals(expectedQuery, filteredQuery.getQuery());
        assertEquals("10", filteredQuery.getParamMap().get("user_id"));
        assertEquals("20", filteredQuery.getParamMap().get("user_id0"));
        assertEquals("serhii", filteredQuery.getParamMap().get("first_name"));
    }

    @Test
    void shouldGenerateWHEREClauseWithRepeatedLogicOperator() {
        String filter = "id=10,OR,id=20,AND,AND,name=serhii";

        String expectedQuery = commonQueryPart + " user_id=:user_id OR user_id=:user_id0 AND first_name=:first_name;";

        QueryHolder filteredQuery = generatorSut.getFindAllQueryWithFilter(properties, filter);
        assertEquals(expectedQuery, filteredQuery.getQuery());
        assertEquals("10", filteredQuery.getParamMap().get("user_id"));
        assertEquals("20", filteredQuery.getParamMap().get("user_id0"));
        assertEquals("serhii", filteredQuery.getParamMap().get("first_name"));
    }

    @Test
    void shouldGenerateWHEREClauseWithBiggerOperator() {
        String filter = "id=10,OR,id>20,AND,name=serhii";

        String expectedQuery = commonQueryPart + " user_id=:user_id OR user_id>:user_id0 AND first_name=:first_name;";

        QueryHolder filteredQuery = generatorSut.getFindAllQueryWithFilter(properties, filter);
        assertEquals(expectedQuery, filteredQuery.getQuery());
        assertEquals("10", filteredQuery.getParamMap().get("user_id"));
        assertEquals("20", filteredQuery.getParamMap().get("user_id0"));
        assertEquals("serhii", filteredQuery.getParamMap().get("first_name"));
    }

    @Test
    void shouldGenerateWHEREClauseWithComposedOperator() {
        String filter = "id=10,OR,id>=20,AND,name=serhii";

        String expectedQuery = commonQueryPart + " user_id=:user_id OR user_id>=:user_id0 AND first_name=:first_name;";

        QueryHolder filteredQuery = generatorSut.getFindAllQueryWithFilter(properties, filter);
        assertEquals(expectedQuery, filteredQuery.getQuery());
        assertEquals("10", filteredQuery.getParamMap().get("user_id"));
        assertEquals("20", filteredQuery.getParamMap().get("user_id0"));
        assertEquals("serhii", filteredQuery.getParamMap().get("first_name"));
    }

    @Test
    void shouldGenerateWHEREClauseWithLIKEOperator() {
        String filter = "id=10,OR,id>=20,AND,name LIKE serhii";

        String expectedQuery = commonQueryPart + " user_id=:user_id OR user_id>=:user_id0 AND first_name LIKE :first_name;";

        QueryHolder filteredQuery = generatorSut.getFindAllQueryWithFilter(properties, filter);
        assertEquals(expectedQuery, filteredQuery.getQuery());
        assertEquals("10", filteredQuery.getParamMap().get("user_id"));
        assertEquals("20", filteredQuery.getParamMap().get("user_id0"));
        assertEquals("serhii", filteredQuery.getParamMap().get("first_name"));
    }

    @Test
    void shouldGenerateWHEREClauseIsEmpty() {
        String filter = "";

        String expectedQuery = "SELECT user_id, login, first_name, last_name FROM users;";

        QueryHolder filteredQuery = generatorSut.getFindAllQueryWithFilter(properties, filter);
        assertEquals(expectedQuery, filteredQuery.getQuery());
    }
}