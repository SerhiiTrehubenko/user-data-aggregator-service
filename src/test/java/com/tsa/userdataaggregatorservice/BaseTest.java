package com.tsa.userdataaggregatorservice;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;

@SpringBootTest
@ActiveProfiles({"test"})
public abstract class BaseTest {
    protected final static File DOCKER_COMPOSE = new File("docker-compose.yml");
}
