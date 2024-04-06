package com.spring.library.repository;

import org.springframework.stereotype.Repository;

import com.spring.library.model.User;

@Repository
public class UserRepository {
    public User findUserByEmail(String email) {
        User user = new User(email, "123456");
        user.setFirstName("FirstName");
        user.setLastName("LastName");
        return user;
    }
}