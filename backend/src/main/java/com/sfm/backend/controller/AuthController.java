package com.sfm.backend.controller;

import com.sfm.backend.dto.ErrorResponse;
import com.sfm.backend.dto.LoginDto;
import com.sfm.backend.dto.RegistrationDto;
import com.sfm.backend.dto.SimpleMessageResponse;
import com.sfm.backend.service.UserService;
import com.sfm.backend.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"}, allowCredentials = "true")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @Operation(summary = "Regisztráció új felhasználó létrehozásához")
    @ApiResponse(responseCode = "201", description = "Sikeres regisztráció", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleMessageResponse.class)))
    @ApiResponse(responseCode = "400", description = "Érvényességi vagy üzleti hiba", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping("/registration")
    public ResponseEntity<?> register(@Valid @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(schema = @Schema(implementation = RegistrationDto.class))) @RequestBody RegistrationDto dto) {
        try {
            userService.register(dto);
            Map<String, String> resp = new HashMap<>();
            resp.put("message", "Sikeres regisztráció!");
            return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        } catch (IllegalArgumentException ex) {
            Map<String, String> err = new HashMap<>();
            err.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        }
    }

    @Operation(summary = "Bejelentkezés - visszaad egy Bearer JWT tokent siker esetén")
    @ApiResponse(responseCode = "200", description = "Sikeres bejelentkezés", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleMessageResponse.class)))
    @ApiResponse(responseCode = "400", description = "Hibás hitelesítési adatok vagy egyéb hiba", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(schema = @Schema(implementation = LoginDto.class))) @RequestBody LoginDto dto) {
        var userOpt = userService.login(dto);
        if (userOpt.isPresent()) {
            var user = userOpt.get();
            String token = jwtUtil.generateToken(user.getUsername());
            Map<String, Object> resp = new HashMap<>();
            resp.put("token", token);
            resp.put("tokenType", "Bearer");
            resp.put("username", user.getUsername());
            resp.put("email", user.getEmail());
            resp.put("name", user.getFullName());
            return ResponseEntity.ok(resp);
        } else {
            Map<String, String> err = new HashMap<>();
            err.put("error", "A megadott felhasználónév/email és jelszó kombinációra nincs egyezés.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        }
    }
}
