package com.sfm.backend.service;

import com.sfm.backend.dto.BookingDto;
import com.sfm.backend.model.*;
import com.sfm.backend.repository.BookingRepository;
import com.sfm.backend.repository.RoomRepository;
import com.sfm.backend.repository.UserRepository;
import com.sfm.backend.service.impl.BookingServiceImpl;
import com.sfm.backend.util.PasswordUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@DisplayName("Unit tesztek a BookingService-hez (Gherkin style)")
public class BookingServiceGherkinTest {

    @Test
    @DisplayName("Given free room and valid slot, when book called, then booking created")
    void givenFreeRoom_whenBook_thenCreated() {
        UserRepository userRepo = Mockito.mock(UserRepository.class);
        RoomRepository roomRepo = Mockito.mock(RoomRepository.class);
        BookingRepository bookingRepo = Mockito.mock(BookingRepository.class);

        User user = new User(1L, "demo", PasswordUtil.encode("password123"), "demo@example.com", "Demo");
        when(userRepo.findByUsername("demo")).thenReturn(Optional.of(user));
        Room room = new Room(1L, "ROOM-1", "Terem 1", 10, "Epulet 1");
        when(roomRepo.findByCode("ROOM-1")).thenReturn(Optional.of(room));

        // No overlapping bookings
        when(bookingRepo.findByRoomAndStartTimeLessThanAndEndTimeGreaterThanAndStatus(Mockito.eq(room), Mockito.any(), Mockito.any(), Mockito.eq(BookingStatus.ACTIVE)))
                .thenReturn(Collections.emptyList());

        BookingServiceImpl svc = new BookingServiceImpl(bookingRepo, roomRepo, userRepo);

        BookingDto dto = new BookingDto();
        dto.setRoomCode("ROOM-1");
        dto.setStartTime(LocalDateTime.now().plusDays(1).withMinute(0).withSecond(0).withNano(0).toString());
        dto.setEndTime(LocalDateTime.now().plusDays(1).withMinute(30).withSecond(0).withNano(0).toString());

        Booking b = svc.book("demo", dto);
        assertNotNull(b);
        assertEquals(user.getId(), b.getUser().getId());
        assertEquals(room.getId(), b.getRoom().getId());
    }

    @Test
    @DisplayName("Given overlapping booking exists, when book called, then IllegalArgumentException")
    void givenOverlap_whenBook_thenError() {
        UserRepository userRepo = Mockito.mock(UserRepository.class);
        RoomRepository roomRepo = Mockito.mock(RoomRepository.class);
        BookingRepository bookingRepo = Mockito.mock(BookingRepository.class);

        User user = new User(1L, "demo", PasswordUtil.encode("password123"), "demo@example.com", "Demo");
        when(userRepo.findByUsername("demo")).thenReturn(Optional.of(user));
        Room room = new Room(1L, "ROOM-1", "Terem 1", 10, "Epulet 1");
        when(roomRepo.findByCode("ROOM-1")).thenReturn(Optional.of(room));

        Booking existing = new Booking(5L, user, room, LocalDateTime.now().plusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0), LocalDateTime.now().plusDays(1).withHour(9).withMinute(30).withSecond(0).withNano(0), BookingStatus.ACTIVE);
        when(bookingRepo.findByRoomAndStartTimeLessThanAndEndTimeGreaterThanAndStatus(Mockito.eq(room), Mockito.any(), Mockito.any(), Mockito.eq(BookingStatus.ACTIVE)))
                .thenReturn(Collections.singletonList(existing));

        BookingServiceImpl svc = new BookingServiceImpl(bookingRepo, roomRepo, userRepo);

        BookingDto dto = new BookingDto();
        dto.setRoomCode("ROOM-1");
        dto.setStartTime(LocalDateTime.now().plusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0).toString());
        dto.setEndTime(LocalDateTime.now().plusDays(1).withHour(9).withMinute(30).withSecond(0).withNano(0).toString());

        assertThrows(IllegalArgumentException.class, () -> svc.book("demo", dto));
    }
}

