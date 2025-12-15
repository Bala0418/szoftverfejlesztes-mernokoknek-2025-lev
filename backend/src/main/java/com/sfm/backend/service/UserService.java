package com.sfm.backend.service;

import com.sfm.backend.dto.LoginDto;
import com.sfm.backend.dto.RegistrationDto;
import com.sfm.backend.model.User;

import java.util.Optional;

public interface UserService {
    User register(RegistrationDto dto) throws IllegalArgumentException;
    Optional<User> login(LoginDto dto);
    Optional<User> findByUsername(String username);
}

