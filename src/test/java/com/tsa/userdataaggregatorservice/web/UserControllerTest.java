package com.tsa.userdataaggregatorservice.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class UserControllerTest
        extends AbstractContainersWebTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void shouldFindAllUsers() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(getContent("mock/all-users.json"), true));
    }

    @Test
    void shouldFindWithEqualsOperator() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/users/filter")
                        .queryParam("where", "id=example-user-id-1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(getContent("mock/equal-operator-users.json"), true));
    }

    @Test
    void shouldFindWithEqualsWithOROperators() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/users/filter")
                        .queryParam("where", "id=example-user-id-1,OR,id=example-user-id-3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(getContent("mock/equal-or-operator-users.json"), true));
    }

    @Test
    void shouldFindWithEqualsWithANDOperators() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/users/filter")
                        .queryParam("where", "id>=example-user-id-1,AND,username<=login-3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(getContent("mock/equal-and-operator-users.json"), true));
    }

    @Test
    void shouldFindWithLIKEOperator() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/users/filter")
                        .queryParam("where", "id LIKE %user-id-1,OR,id LIKE %user-id-3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(getContent("mock/like-operator-users.json"), true));
    }

    @Test
    void shouldThrowExceptionOnWrongParameter() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/users/filter")
                        .queryParam("where", "user-id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(getContent("mock/wrong-parameter.json"), true));
    }

    private String getContent(String filePath) {
        try (InputStream file = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath)) {
            Objects.requireNonNull(file);
            return new String(file.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("File with path: [%s] was not found".formatted(filePath), e);
        }
    }
}