package com.tsa.userdataaggregatorservice.domain;

import java.util.List;

public interface UserService {
    List<User> findAll();

    List<User> findAllFilter(String filterClause);
}
