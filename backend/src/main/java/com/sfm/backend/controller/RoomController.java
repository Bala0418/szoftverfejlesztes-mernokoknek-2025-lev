package com.sfm.backend.controller;

import com.sfm.backend.dto.RoomResponse;
import com.sfm.backend.model.Booking;
import com.sfm.backend.model.BookingStatus;
import com.sfm.backend.model.Room;
import com.sfm.backend.repository.BookingRepository;
import com.sfm.backend.repository.RoomRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    public RoomController(RoomRepository roomRepository, BookingRepository bookingRepository) {
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
    }

    @Operation(summary = "Listázza a termeket (szűrhető)")
    @ApiResponse(responseCode = "200", description = "A termek listája (szűrve)", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = RoomResponse.class))))
    @GetMapping
    public List<RoomResponse> listRooms(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String from, // HH-mm
            @RequestParam(required = false) String to,   // HH-mm
            @RequestParam(required = false) Integer capacity,
            @RequestParam(required = false) List<String> equipment
    ) {
        List<Room> rooms = roomRepository.findAll();

        if (capacity != null) {
            rooms = rooms.stream().filter(r -> r.getCapacity() != null && r.getCapacity() >= capacity).collect(Collectors.toList());
        }

        if (equipment != null && !equipment.isEmpty()) {
            List<String> lowerReq = equipment.stream().map(s -> s.toLowerCase(Locale.ROOT)).collect(Collectors.toList());
            rooms = rooms.stream().filter(r -> {
                List<String> feats = r.getFeatures();
                if (feats == null) return false;
                List<String> lowerFeats = feats.stream().map(f -> f.toLowerCase(Locale.ROOT)).collect(Collectors.toList());
                return lowerReq.stream().allMatch(lowerFeats::contains);
            }).collect(Collectors.toList());
        }

        if (date != null && from != null && to != null) {
            LocalTime tFrom = LocalTime.parse(from.replace('-', ':'));
            LocalTime tTo = LocalTime.parse(to.replace('-', ':'));
            LocalDateTime start = LocalDateTime.of(date, tFrom);
            LocalDateTime end = LocalDateTime.of(date, tTo);

            rooms = rooms.stream().filter(r -> {
                List<Booking> overlaps = bookingRepository.findByRoomAndStartTimeLessThanAndEndTimeGreaterThanAndStatus(r, end, start, BookingStatus.ACTIVE);
                return overlaps.isEmpty();
            }).collect(Collectors.toList());
        }

        return rooms.stream().map(r -> new RoomResponse(r.getId(), r.getCode(), r.getName(), r.getCapacity(), r.getLocation(), r.getFeatures())).collect(Collectors.toList());
    }
}
