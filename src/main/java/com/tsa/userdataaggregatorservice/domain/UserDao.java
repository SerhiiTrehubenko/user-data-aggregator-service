package com.tsa.userdataaggregatorservice.domain;

import java.util.List;

public interface UserDao {
    List<User> findAll();

    List<User> findAllFilter(String filter);
}
