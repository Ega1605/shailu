package com.shailu.deposito_dental_pos.service;

import com.shailu.deposito_dental_pos.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public boolean login(String username, String password) {

        return userRepository
                .findByUsernameAndPassword(username, password)
                .isPresent();
    }
}
