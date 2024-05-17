package com.tsa.userdataaggregatorservice.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceDefault implements UserService {
    private final UserDao userDao;

    @Override
    public List<User> findAll() {
        return userDao.findAll();
    }
}
