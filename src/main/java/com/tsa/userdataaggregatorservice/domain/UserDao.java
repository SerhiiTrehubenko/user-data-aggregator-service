package com.tsa.userdataaggregatorservice.domain;

import java.util.List;

public interface UserDao {
    List<User> findAll();
}
