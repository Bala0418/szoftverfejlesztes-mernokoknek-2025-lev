package com.sfm.backend.service.impl;

import com.sfm.backend.dto.LoginDto;
import com.sfm.backend.dto.RegistrationDto;
import com.sfm.backend.model.User;
import com.sfm.backend.repository.UserRepository;
import com.sfm.backend.service.UserService;
import com.sfm.backend.util.PasswordUtil;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User register(RegistrationDto dto) throws IllegalArgumentException {
        if (dto.getPassword().equals(dto.getUsername())) {
            throw new IllegalArgumentException("A jelszó nem egyezhet meg a felhasználónévvel.");
        }

        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("A felhasználónév már foglalt.");
        }

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Az email cím már használatban van.");
        }

        String hashed = PasswordUtil.encode(dto.getPassword());
        User user = new User(null, dto.getUsername(), hashed, dto.getEmail(), dto.getFullName());
        return userRepository.save(user);
    }

    @Override
    public Optional<User> login(LoginDto dto) {
        Optional<User> byUsername = userRepository.findByUsername(dto.getUsernameOrEmail());
        Optional<User> byEmail = userRepository.findByEmail(dto.getUsernameOrEmail());

        Optional<User> user = byUsername.isPresent() ? byUsername : byEmail;

        if (user.isEmpty()) return Optional.empty();

        User u = user.get();
        if (PasswordUtil.matches(dto.getPassword(), u.getPasswordHash())) {
            return Optional.of(u);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
