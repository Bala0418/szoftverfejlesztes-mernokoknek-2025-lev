package com.sfm.backend.controller;

import com.sfm.backend.dto.ModifyContactInfoDto;
import com.sfm.backend.exception.UnauthenticatedException;
import com.sfm.backend.model.User;
import com.sfm.backend.service.UserService;
import com.sfm.backend.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    private String currentUsernameFromAuthHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthenticatedException("Authorization header missing or invalid.");
        }
        String token = authHeader.substring("Bearer ".length());
        String username = jwtUtil.validateAndGetUsername(token);
        if (username == null) throw new UnauthenticatedException("A token érvénytelen vagy lejárt.");
        return username;
    }

    @Operation(summary = "Felhasználói profil módosítása")
    @ApiResponse(responseCode = "200", description = "Sikeres módosítás")
    @ApiResponse(responseCode = "400", description = "Érvényességi hiba vagy hibás jelszó")
    @ApiResponse(responseCode = "403", description = "Nem saját profil módosítási kísérlet")
    @PutMapping("/{username}/modify-contact-info")
    public ResponseEntity<?> modifyContactInfo(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable String username,
            @Valid @RequestBody ModifyContactInfoDto dto) {
        try {
            String authenticatedUsername = currentUsernameFromAuthHeader(authHeader);
            
            // Csak saját profilt lehet módosítani
            if (!authenticatedUsername.equals(username)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Csak a saját profilodat módosíthatod.");
                return ResponseEntity.status(403).body(error);
            }

            User updatedUser = userService.modifyContactInfo(username, dto);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "A profil sikeresen módosítva.");
            response.put("username", updatedUser.getUsername());
            response.put("email", updatedUser.getEmail());
            response.put("fullName", updatedUser.getFullName());
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", ex.getMessage());
            return ResponseEntity.status(400).body(error);
        }
    }
}
