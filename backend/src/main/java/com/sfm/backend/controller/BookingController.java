package com.sfm.backend.controller;

import com.sfm.backend.dto.BookingDto;
import com.sfm.backend.dto.BookingResponse;
import com.sfm.backend.dto.ErrorResponse;
import com.sfm.backend.dto.SimpleMessageResponse;
import com.sfm.backend.exception.UnauthenticatedException;
import com.sfm.backend.service.BookingService;
import com.sfm.backend.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final JwtUtil jwtUtil;

    public BookingController(BookingService bookingService, JwtUtil jwtUtil) {
        this.bookingService = bookingService;
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

    @Operation(summary = "Foglalás létrehozása")
    @ApiResponse(responseCode = "201", description = "Sikeres teremfoglalás", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleMessageResponse.class)))
    @ApiResponse(responseCode = "400", description = "Érvényességi vagy üzleti hiba", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping
    public ResponseEntity<?> book(@RequestHeader(value = "Authorization", required = false) String authHeader, @Valid @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(schema = @Schema(implementation = BookingDto.class))) @RequestBody BookingDto dto) {
        try {
            String username = currentUsernameFromAuthHeader(authHeader);
            var b = bookingService.book(username, dto);
            Map<String, Object> resp = new HashMap<>();
            resp.put("message", "Sikeres teremfoglalás!");
            resp.put("bookingId", b.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        } catch (IllegalArgumentException ex) {
            Map<String, String> err = new HashMap<>();
            err.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        }
    }

    @Operation(summary = "Foglalás módosítása")
    @ApiResponse(responseCode = "200", description = "Sikeres módosítás", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleMessageResponse.class)))
    @ApiResponse(responseCode = "400", description = "Érvényességi vagy jogosultsági hiba", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestHeader(value = "Authorization", required = false) String authHeader, @PathVariable Long id, @Valid @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(schema = @Schema(implementation = BookingDto.class))) @RequestBody BookingDto dto) {
        try {
            String username = currentUsernameFromAuthHeader(authHeader);
            var b = bookingService.update(username, id, dto);
            Map<String, Object> resp = new HashMap<>();
            resp.put("message", "Sikeres módosítás!");
            resp.put("bookingId", b.getId());
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException ex) {
            Map<String, String> err = new HashMap<>();
            err.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        }
    }

    @Operation(summary = "Foglalás lemondása")
    @ApiResponse(responseCode = "200", description = "Sikeres lemondás", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleMessageResponse.class)))
    @ApiResponse(responseCode = "400", description = "Érvényességi vagy jogosultsági hiba", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancel(@RequestHeader(value = "Authorization", required = false) String authHeader, @PathVariable Long id) {
        try {
            String username = currentUsernameFromAuthHeader(authHeader);
            bookingService.cancel(username, id);
            Map<String, String> resp = new HashMap<>();
            resp.put("message", "A foglalás sikeresen lemondva.");
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException ex) {
            Map<String, String> err = new HashMap<>();
            err.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        }
    }

    @Operation(summary = "Aktív foglalások lekérése az adott felhasználóra")
    @ApiResponse(responseCode = "200", description = "Az aktív foglalások listája", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BookingResponse.class))))
    @GetMapping("/active")
    public List<BookingResponse> active(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        String username = currentUsernameFromAuthHeader(authHeader);
        return bookingService.activeBookingsForUser(username);
    }

    @Operation(summary = "Historikus foglalások lekérése az adott felhasználóra")
    @ApiResponse(responseCode = "200", description = "A historikus foglalások listája", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BookingResponse.class))))
    @GetMapping("/history")
    public List<BookingResponse> history(@RequestHeader(value = "Authorization", required = false) String authHeader, @RequestParam(defaultValue = "5") int limit) {
        String username = currentUsernameFromAuthHeader(authHeader);
        return bookingService.historicalBookingsForUser(username, limit);
    }
}
