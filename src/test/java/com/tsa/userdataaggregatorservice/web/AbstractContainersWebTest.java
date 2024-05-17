package com.tsa.userdataaggregatorservice.web;

import com.tsa.userdataaggregatorservice.BaseTest;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class AbstractContainersWebTest extends BaseTest {

    @Container
    private static final DockerComposeContainer<?> COMPOSE_CONTAINER;

    static {
        COMPOSE_CONTAINER = new DockerComposeContainer<>(DOCKER_COMPOSE)

                .withExposedService("postgresdb", 5432)
                .withExposedService("mysqldb", 3306);
    }
}
