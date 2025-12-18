package com.sfm.backend.service;

import com.sfm.backend.dto.LoginDto;
import com.sfm.backend.dto.RegistrationDto;
import com.sfm.backend.model.User;
import com.sfm.backend.repository.UserRepository;
import com.sfm.backend.service.impl.UserServiceImpl;
import com.sfm.backend.util.PasswordUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@DisplayName("Unit tesztek a UserService-hez (Gherkin style)")
public class UserServiceGherkinTest {

    @Test
    @DisplayName("Given új felhasználó adatok, when register called, then user is created")
    void givenNewUser_whenRegister_thenCreated() {
        // Given
        UserRepository userRepo = Mockito.mock(UserRepository.class);
        when(userRepo.findByUsername("bela")).thenReturn(Optional.empty());
        when(userRepo.findByEmail("bela@example.com")).thenReturn(Optional.empty());

        RegistrationDto dto = new RegistrationDto();
        dto.setUsername("bela");
        dto.setFullName("Béla Nagy");
        dto.setEmail("bela@example.com");
        dto.setPassword("securePass1");

        User saved = new User(1L, dto.getUsername(), PasswordUtil.encode(dto.getPassword()), dto.getEmail(), dto.getFullName());
        when(userRepo.save(Mockito.any())).thenReturn(saved);

        UserServiceImpl svc = new UserServiceImpl(userRepo);

        // When
        User result = svc.register(dto);

        // Then
        assertNotNull(result);
        assertEquals("bela", result.getUsername());
        assertEquals("bela@example.com", result.getEmail());
    }

    @Test
    @DisplayName("Given létező felhasználó, when register with same username, then IllegalArgumentException")
    void givenExistingUser_whenRegisterSameUsername_thenError() {
        UserRepository userRepo = Mockito.mock(UserRepository.class);
        when(userRepo.findByUsername("bela")).thenReturn(Optional.of(new User(2L, "bela", "x", "bela2@example.com", "Béla")));

        RegistrationDto dto = new RegistrationDto();
        dto.setUsername("bela");
        dto.setFullName("Béla");
        dto.setEmail("bela@example.com");
        dto.setPassword("securePass1");

        UserServiceImpl svc = new UserServiceImpl(userRepo);

        assertThrows(IllegalArgumentException.class, () -> svc.register(dto));
    }

    @Test
    @DisplayName("Given user exists, when login with correct password, then success")
    void givenUser_whenLoginCorrectPassword_thenSuccess() {
        UserRepository userRepo = Mockito.mock(UserRepository.class);
        String hash = PasswordUtil.encode("password123");
        when(userRepo.findByUsername("demo")).thenReturn(Optional.of(new User(1L, "demo", hash, "demo@example.com", "Demo")));
        when(userRepo.findByEmail("demo@example.com")).thenReturn(Optional.empty());

        LoginDto dto = new LoginDto();
        dto.setUsernameOrEmail("demo");
        dto.setPassword("password123");

        UserServiceImpl svc = new UserServiceImpl(userRepo);
        var res = svc.login(dto);
        assertTrue(res.isPresent());
        assertEquals("demo", res.get().getUsername());
    }

    @Test
    @DisplayName("Given user exists, when login with wrong password, then empty result")
    void givenUser_whenLoginWrongPassword_thenEmpty() {
        UserRepository userRepo = Mockito.mock(UserRepository.class);
        String hash = PasswordUtil.encode("password123");
        when(userRepo.findByUsername("demo")).thenReturn(Optional.of(new User(1L, "demo", hash, "demo@example.com", "Demo")));

        LoginDto dto = new LoginDto();
        dto.setUsernameOrEmail("demo");
        dto.setPassword("wrongpass");

        UserServiceImpl svc = new UserServiceImpl(userRepo);
        var res = svc.login(dto);
        assertTrue(res.isEmpty());
    }

    @Test
    @DisplayName("Given user exists, when login with email, then success")
    void givenUser_whenLoginWithEmail_thenSuccess() {
        UserRepository userRepo = Mockito.mock(UserRepository.class);
        String hash = PasswordUtil.encode("password123");
        User user = new User(1L, "demo", hash, "demo@example.com", "Demo");
        when(userRepo.findByUsername("demo@example.com")).thenReturn(Optional.empty());
        when(userRepo.findByEmail("demo@example.com")).thenReturn(Optional.of(user));

        LoginDto dto = new LoginDto();
        dto.setUsernameOrEmail("demo@example.com");
        dto.setPassword("password123");

        UserServiceImpl svc = new UserServiceImpl(userRepo);
        var res = svc.login(dto);
        assertTrue(res.isPresent());
        assertEquals("demo", res.get().getUsername());
    }

    @Test
    @DisplayName("Given duplicate email, when register, then IllegalArgumentException")
    void givenDuplicateEmail_whenRegister_thenError() {
        UserRepository userRepo = Mockito.mock(UserRepository.class);
        when(userRepo.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userRepo.findByEmail("existing@example.com")).thenReturn(Optional.of(new User(2L, "other", "hash", "existing@example.com", "Other")));

        RegistrationDto dto = new RegistrationDto();
        dto.setUsername("newuser");
        dto.setFullName("New User");
        dto.setEmail("existing@example.com");
        dto.setPassword("securePass1");

        UserServiceImpl svc = new UserServiceImpl(userRepo);

        assertThrows(IllegalArgumentException.class, () -> svc.register(dto));
    }

    @Test
    @DisplayName("Given password equals username, when register, then IllegalArgumentException")
    void givenPasswordEqualsUsername_whenRegister_thenError() {
        UserRepository userRepo = Mockito.mock(UserRepository.class);
        when(userRepo.findByUsername("bela")).thenReturn(Optional.empty());
        when(userRepo.findByEmail("bela@example.com")).thenReturn(Optional.empty());

        RegistrationDto dto = new RegistrationDto();
        dto.setUsername("bela");
        dto.setFullName("Béla Nagy");
        dto.setEmail("bela@example.com");
        dto.setPassword("bela"); // Same as username

        UserServiceImpl svc = new UserServiceImpl(userRepo);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> svc.register(dto));
        assertTrue(ex.getMessage().contains("jelszó nem egyezhet meg"));
    }

    @Test
    @DisplayName("Given non-existing user, when login, then empty result")
    void givenNonExistingUser_whenLogin_thenEmpty() {
        UserRepository userRepo = Mockito.mock(UserRepository.class);
        when(userRepo.findByUsername("nonexist")).thenReturn(Optional.empty());
        when(userRepo.findByEmail("nonexist")).thenReturn(Optional.empty());

        LoginDto dto = new LoginDto();
        dto.setUsernameOrEmail("nonexist");
        dto.setPassword("password");

        UserServiceImpl svc = new UserServiceImpl(userRepo);
        var res = svc.login(dto);
        assertTrue(res.isEmpty());
    }

    @Test
    @DisplayName("Given username, when findByUsername, then user returned")
    void givenUsername_whenFindByUsername_thenUserReturned() {
        UserRepository userRepo = Mockito.mock(UserRepository.class);
        User user = new User(1L, "demo", "hash", "demo@example.com", "Demo");
        when(userRepo.findByUsername("demo")).thenReturn(Optional.of(user));

        UserServiceImpl svc = new UserServiceImpl(userRepo);
        var res = svc.findByUsername("demo");
        assertTrue(res.isPresent());
        assertEquals("demo", res.get().getUsername());
    }
}

