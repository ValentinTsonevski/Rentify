package com.devminds.rentify.service;

import com.devminds.rentify.entity.User;

import java.util.Optional;

public interface UserService {
    User saveUser(User user);
    Optional<User> findByEmail(String email);

    public Optional<User> findById(long id);
}
