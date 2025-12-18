package com.sfm.backend.service;

import com.sfm.backend.dto.ModifyContactInfoDto;
import com.sfm.backend.model.User;
import com.sfm.backend.repository.UserRepository;
import com.sfm.backend.service.impl.UserServiceImpl;
import com.sfm.backend.util.PasswordUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Unit tesztek a UserService modifyContactInfo metódusához")
public class UserServiceModifyContactInfoTest {

    @Test
    @DisplayName("Given valid current password, when modify fullName, then fullName updated")
    void givenValidPassword_whenModifyFullName_thenUpdated() {
        // Given
        UserRepository userRepo = Mockito.mock(UserRepository.class);
        String hash = PasswordUtil.encode("password123");
        User user = new User(1L, "demo", hash, "demo@example.com", "Old Name");
        when(userRepo.findByUsername("demo")).thenReturn(Optional.of(user));
        when(userRepo.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ModifyContactInfoDto dto = new ModifyContactInfoDto();
        dto.setCurrentPassword("password123");
        dto.setFullName("New Name");

        UserServiceImpl svc = new UserServiceImpl(userRepo);

        // When
        User result = svc.modifyContactInfo("demo", dto);

        // Then
        assertNotNull(result);
        assertEquals("New Name", result.getFullName());
        assertEquals("demo@example.com", result.getEmail()); // unchanged
        verify(userRepo, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Given wrong current password, when modify, then IllegalArgumentException")
    void givenWrongPassword_whenModify_thenError() {
        // Given
        UserRepository userRepo = Mockito.mock(UserRepository.class);
        String hash = PasswordUtil.encode("password123");
        User user = new User(1L, "demo", hash, "demo@example.com", "Demo");
        when(userRepo.findByUsername("demo")).thenReturn(Optional.of(user));

        ModifyContactInfoDto dto = new ModifyContactInfoDto();
        dto.setCurrentPassword("wrongpassword");
        dto.setFullName("New Name");

        UserServiceImpl svc = new UserServiceImpl(userRepo);

        // When & Then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, 
            () -> svc.modifyContactInfo("demo", dto));
        assertTrue(ex.getMessage().contains("jelenlegi jelszó helytelen"));
    }

    @Test
    @DisplayName("Given valid password, when modify email, then email updated")
    void givenValidPassword_whenModifyEmail_thenUpdated() {
        // Given
        UserRepository userRepo = Mockito.mock(UserRepository.class);
        String hash = PasswordUtil.encode("password123");
        User user = new User(1L, "demo", hash, "old@example.com", "Demo");
        when(userRepo.findByUsername("demo")).thenReturn(Optional.of(user));
        when(userRepo.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(userRepo.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ModifyContactInfoDto dto = new ModifyContactInfoDto();
        dto.setCurrentPassword("password123");
        dto.setEmail("new@example.com");

        UserServiceImpl svc = new UserServiceImpl(userRepo);

        // When
        User result = svc.modifyContactInfo("demo", dto);

        // Then
        assertEquals("new@example.com", result.getEmail());
        verify(userRepo, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Given email already taken by other user, when modify, then IllegalArgumentException")
    void givenEmailTaken_whenModify_thenError() {
        // Given
        UserRepository userRepo = Mockito.mock(UserRepository.class);
        String hash = PasswordUtil.encode("password123");
        User user = new User(1L, "demo", hash, "demo@example.com", "Demo");
        User otherUser = new User(2L, "other", "hash", "taken@example.com", "Other");
        when(userRepo.findByUsername("demo")).thenReturn(Optional.of(user));
        when(userRepo.findByEmail("taken@example.com")).thenReturn(Optional.of(otherUser));

        ModifyContactInfoDto dto = new ModifyContactInfoDto();
        dto.setCurrentPassword("password123");
        dto.setEmail("taken@example.com");

        UserServiceImpl svc = new UserServiceImpl(userRepo);

        // When & Then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, 
            () -> svc.modifyContactInfo("demo", dto));
        assertTrue(ex.getMessage().contains("email cím már használatban"));
    }

    @Test
    @DisplayName("Given valid password, when modify to same email, then no error")
    void givenSameEmail_whenModify_thenNoError() {
        // Given
        UserRepository userRepo = Mockito.mock(UserRepository.class);
        String hash = PasswordUtil.encode("password123");
        User user = new User(1L, "demo", hash, "demo@example.com", "Demo");
        when(userRepo.findByUsername("demo")).thenReturn(Optional.of(user));
        when(userRepo.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ModifyContactInfoDto dto = new ModifyContactInfoDto();
        dto.setCurrentPassword("password123");
        dto.setEmail("demo@example.com"); // Same email

        UserServiceImpl svc = new UserServiceImpl(userRepo);

        // When
        User result = svc.modifyContactInfo("demo", dto);

        // Then
        assertNotNull(result);
        assertEquals("demo@example.com", result.getEmail());
    }

    @Test
    @DisplayName("Given valid password, when modify password, then password hash updated")
    void givenValidPassword_whenModifyPassword_thenUpdated() {
        // Given
        UserRepository userRepo = Mockito.mock(UserRepository.class);
        String hash = PasswordUtil.encode("oldpassword");
        User user = new User(1L, "demo", hash, "demo@example.com", "Demo");
        when(userRepo.findByUsername("demo")).thenReturn(Optional.of(user));
        when(userRepo.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ModifyContactInfoDto dto = new ModifyContactInfoDto();
        dto.setCurrentPassword("oldpassword");
        dto.setNewPassword("newpassword123");

        UserServiceImpl svc = new UserServiceImpl(userRepo);

        // When
        User result = svc.modifyContactInfo("demo", dto);

        // Then
        assertNotNull(result);
        assertNotEquals(hash, result.getPasswordHash());
        assertTrue(PasswordUtil.matches("newpassword123", result.getPasswordHash()));
    }

    @Test
    @DisplayName("Given new password equals username, when modify, then IllegalArgumentException")
    void givenPasswordEqualsUsername_whenModify_thenError() {
        // Given
        UserRepository userRepo = Mockito.mock(UserRepository.class);
        String hash = PasswordUtil.encode("oldpassword");
        User user = new User(1L, "demo", hash, "demo@example.com", "Demo");
        when(userRepo.findByUsername("demo")).thenReturn(Optional.of(user));

        ModifyContactInfoDto dto = new ModifyContactInfoDto();
        dto.setCurrentPassword("oldpassword");
        dto.setNewPassword("demo"); // Same as username

        UserServiceImpl svc = new UserServiceImpl(userRepo);

        // When & Then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, 
            () -> svc.modifyContactInfo("demo", dto));
        assertTrue(ex.getMessage().contains("jelszó nem egyezhet meg"));
    }

    @Test
    @DisplayName("Given non-existing user, when modify, then IllegalArgumentException")
    void givenNonExistingUser_whenModify_thenError() {
        // Given
        UserRepository userRepo = Mockito.mock(UserRepository.class);
        when(userRepo.findByUsername("nonexist")).thenReturn(Optional.empty());

        ModifyContactInfoDto dto = new ModifyContactInfoDto();
        dto.setCurrentPassword("password");
        dto.setFullName("New Name");

        UserServiceImpl svc = new UserServiceImpl(userRepo);

        // When & Then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, 
            () -> svc.modifyContactInfo("nonexist", dto));
        assertTrue(ex.getMessage().contains("felhasználó nem található"));
    }

    @Test
    @DisplayName("Given valid password, when modify all fields, then all updated")
    void givenValidPassword_whenModifyAllFields_thenAllUpdated() {
        // Given
        UserRepository userRepo = Mockito.mock(UserRepository.class);
        String hash = PasswordUtil.encode("oldpassword");
        User user = new User(1L, "demo", hash, "old@example.com", "Old Name");
        when(userRepo.findByUsername("demo")).thenReturn(Optional.of(user));
        when(userRepo.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(userRepo.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ModifyContactInfoDto dto = new ModifyContactInfoDto();
        dto.setCurrentPassword("oldpassword");
        dto.setFullName("New Name");
        dto.setEmail("new@example.com");
        dto.setNewPassword("newpassword123");

        UserServiceImpl svc = new UserServiceImpl(userRepo);

        // When
        User result = svc.modifyContactInfo("demo", dto);

        // Then
        assertEquals("New Name", result.getFullName());
        assertEquals("new@example.com", result.getEmail());
        assertTrue(PasswordUtil.matches("newpassword123", result.getPasswordHash()));
    }

    @Test
    @DisplayName("Given blank fullName, when modify, then fullName unchanged")
    void givenBlankFullName_whenModify_thenUnchanged() {
        // Given
        UserRepository userRepo = Mockito.mock(UserRepository.class);
        String hash = PasswordUtil.encode("password123");
        User user = new User(1L, "demo", hash, "demo@example.com", "Old Name");
        when(userRepo.findByUsername("demo")).thenReturn(Optional.of(user));
        when(userRepo.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ModifyContactInfoDto dto = new ModifyContactInfoDto();
        dto.setCurrentPassword("password123");
        dto.setFullName("   "); // Blank

        UserServiceImpl svc = new UserServiceImpl(userRepo);

        // When
        User result = svc.modifyContactInfo("demo", dto);

        // Then
        assertEquals("Old Name", result.getFullName()); // Unchanged
    }
}
