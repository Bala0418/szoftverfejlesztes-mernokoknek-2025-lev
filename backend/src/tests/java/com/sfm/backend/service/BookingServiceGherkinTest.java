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

    @Test
    @DisplayName("Given non-existing user, when book, then IllegalArgumentException")
    void givenNonExistingUser_whenBook_thenError() {
        UserRepository userRepo = Mockito.mock(UserRepository.class);
        RoomRepository roomRepo = Mockito.mock(RoomRepository.class);
        BookingRepository bookingRepo = Mockito.mock(BookingRepository.class);

        when(userRepo.findByUsername("nonexist")).thenReturn(Optional.empty());

        BookingServiceImpl svc = new BookingServiceImpl(bookingRepo, roomRepo, userRepo);

        BookingDto dto = new BookingDto();
        dto.setRoomCode("ROOM-1");
        dto.setStartTime(LocalDateTime.now().plusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0).toString());
        dto.setEndTime(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0).toString());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> svc.book("nonexist", dto));
        assertTrue(ex.getMessage().contains("felhasználó nem található"));
    }

    @Test
    @DisplayName("Given non-existing room, when book, then IllegalArgumentException")
    void givenNonExistingRoom_whenBook_thenError() {
        UserRepository userRepo = Mockito.mock(UserRepository.class);
        RoomRepository roomRepo = Mockito.mock(RoomRepository.class);
        BookingRepository bookingRepo = Mockito.mock(BookingRepository.class);

        User user = new User(1L, "demo", PasswordUtil.encode("password123"), "demo@example.com", "Demo");
        when(userRepo.findByUsername("demo")).thenReturn(Optional.of(user));
        when(roomRepo.findByCode("NONEXIST")).thenReturn(Optional.empty());

        BookingServiceImpl svc = new BookingServiceImpl(bookingRepo, roomRepo, userRepo);

        BookingDto dto = new BookingDto();
        dto.setRoomCode("NONEXIST");
        dto.setStartTime(LocalDateTime.now().plusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0).toString());
        dto.setEndTime(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0).toString());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> svc.book("demo", dto));
        assertTrue(ex.getMessage().contains("terem nem található"));
    }

    @Test
    @DisplayName("Given end time before start time, when book, then IllegalArgumentException")
    void givenEndBeforeStart_whenBook_thenError() {
        UserRepository userRepo = Mockito.mock(UserRepository.class);
        RoomRepository roomRepo = Mockito.mock(RoomRepository.class);
        BookingRepository bookingRepo = Mockito.mock(BookingRepository.class);

        User user = new User(1L, "demo", PasswordUtil.encode("password123"), "demo@example.com", "Demo");
        when(userRepo.findByUsername("demo")).thenReturn(Optional.of(user));
        Room room = new Room(1L, "ROOM-1", "Terem 1", 10, "Epulet 1");
        when(roomRepo.findByCode("ROOM-1")).thenReturn(Optional.of(room));

        BookingServiceImpl svc = new BookingServiceImpl(bookingRepo, roomRepo, userRepo);

        BookingDto dto = new BookingDto();
        dto.setRoomCode("ROOM-1");
        dto.setStartTime(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0).toString());
        dto.setEndTime(LocalDateTime.now().plusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0).toString());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> svc.book("demo", dto));
        assertTrue(ex.getMessage().contains("később kell lennie"));
    }

    @Test
    @DisplayName("Given past start time, when book, then IllegalArgumentException")
    void givenPastTime_whenBook_thenError() {
        UserRepository userRepo = Mockito.mock(UserRepository.class);
        RoomRepository roomRepo = Mockito.mock(RoomRepository.class);
        BookingRepository bookingRepo = Mockito.mock(BookingRepository.class);

        User user = new User(1L, "demo", PasswordUtil.encode("password123"), "demo@example.com", "Demo");
        when(userRepo.findByUsername("demo")).thenReturn(Optional.of(user));
        Room room = new Room(1L, "ROOM-1", "Terem 1", 10, "Epulet 1");
        when(roomRepo.findByCode("ROOM-1")).thenReturn(Optional.of(room));

        BookingServiceImpl svc = new BookingServiceImpl(bookingRepo, roomRepo, userRepo);

        BookingDto dto = new BookingDto();
        dto.setRoomCode("ROOM-1");
        dto.setStartTime(LocalDateTime.now().minusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0).toString());
        dto.setEndTime(LocalDateTime.now().minusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0).toString());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> svc.book("demo", dto));
        assertTrue(ex.getMessage().contains("múltbeli"));
    }

    @Test
    @DisplayName("Given valid booking, when cancel called, then status changed to CANCELLED")
    void givenValidBooking_whenCancel_thenStatusChanged() {
        BookingRepository bookingRepo = Mockito.mock(BookingRepository.class);
        UserRepository userRepo = Mockito.mock(UserRepository.class);
        RoomRepository roomRepo = Mockito.mock(RoomRepository.class);

        User user = new User(1L, "demo", PasswordUtil.encode("password123"), "demo@example.com", "Demo");
        Room room = new Room(1L, "ROOM-1", "Terem 1", 10, "Epulet 1");
        Booking booking = new Booking(5L, user, room, LocalDateTime.now().plusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0), LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0), BookingStatus.ACTIVE);

        when(userRepo.findByUsername("demo")).thenReturn(Optional.of(user));
        when(bookingRepo.findById(5L)).thenReturn(Optional.of(booking));
        when(bookingRepo.save(Mockito.any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookingServiceImpl svc = new BookingServiceImpl(bookingRepo, roomRepo, userRepo);

        svc.cancel("demo", 5L);

        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
    }

    @Test
    @DisplayName("Given non-existing booking, when cancel, then IllegalArgumentException")
    void givenNonExistingBooking_whenCancel_thenError() {
        BookingRepository bookingRepo = Mockito.mock(BookingRepository.class);
        UserRepository userRepo = Mockito.mock(UserRepository.class);
        RoomRepository roomRepo = Mockito.mock(RoomRepository.class);

        User user = new User(1L, "demo", PasswordUtil.encode("password123"), "demo@example.com", "Demo");
        when(userRepo.findByUsername("demo")).thenReturn(Optional.of(user));
        when(bookingRepo.findById(999L)).thenReturn(Optional.empty());

        BookingServiceImpl svc = new BookingServiceImpl(bookingRepo, roomRepo, userRepo);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> svc.cancel("demo", 999L));
        assertTrue(ex.getMessage().contains("foglalás nem található"));
    }

    @Test
    @DisplayName("Given already cancelled booking, when cancel again, then no error")
    void givenAlreadyCancelled_whenCancel_thenNoError() {
        BookingRepository bookingRepo = Mockito.mock(BookingRepository.class);
        UserRepository userRepo = Mockito.mock(UserRepository.class);
        RoomRepository roomRepo = Mockito.mock(RoomRepository.class);

        User user = new User(1L, "demo", PasswordUtil.encode("password123"), "demo@example.com", "Demo");
        Room room = new Room(1L, "ROOM-1", "Terem 1", 10, "Epulet 1");
        Booking booking = new Booking(5L, user, room, LocalDateTime.now().plusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0), LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0), BookingStatus.CANCELLED);

        when(userRepo.findByUsername("demo")).thenReturn(Optional.of(user));
        when(bookingRepo.findById(5L)).thenReturn(Optional.of(booking));
        when(bookingRepo.save(Mockito.any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookingServiceImpl svc = new BookingServiceImpl(bookingRepo, roomRepo, userRepo);

        svc.cancel("demo", 5L);

        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
    }

    @Test
    @DisplayName("Given valid update data, when updateBooking, then booking updated")
    void givenValidUpdate_whenUpdateBooking_thenUpdated() {
        BookingRepository bookingRepo = Mockito.mock(BookingRepository.class);
        UserRepository userRepo = Mockito.mock(UserRepository.class);
        RoomRepository roomRepo = Mockito.mock(RoomRepository.class);

        User user = new User(1L, "demo", PasswordUtil.encode("password123"), "demo@example.com", "Demo");
        Room room = new Room(1L, "ROOM-1", "Terem 1", 10, "Epulet 1");
        Booking booking = new Booking(5L, user, room, LocalDateTime.now().plusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0), LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0), BookingStatus.ACTIVE);

        when(userRepo.findByUsername("demo")).thenReturn(Optional.of(user));
        when(roomRepo.findByCode("ROOM-1")).thenReturn(Optional.of(room));
        when(bookingRepo.findById(5L)).thenReturn(Optional.of(booking));
        when(bookingRepo.findByRoomAndStartTimeLessThanAndEndTimeGreaterThanAndStatus(Mockito.eq(room), Mockito.any(), Mockito.any(), Mockito.eq(BookingStatus.ACTIVE)))
                .thenReturn(Collections.emptyList());
        when(bookingRepo.save(Mockito.any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookingServiceImpl svc = new BookingServiceImpl(bookingRepo, roomRepo, userRepo);

        BookingDto dto = new BookingDto();
        dto.setRoomCode("ROOM-1");
        dto.setStartTime(LocalDateTime.now().plusDays(1).withHour(11).withMinute(0).withSecond(0).withNano(0).toString());
        dto.setEndTime(LocalDateTime.now().plusDays(1).withHour(12).withMinute(0).withSecond(0).withNano(0).toString());

        Booking updated = svc.update("demo", 5L, dto);

        assertNotNull(updated);
        assertEquals(LocalDateTime.now().plusDays(1).withHour(11).withMinute(0).withSecond(0).withNano(0), updated.getStartTime());
        assertEquals(LocalDateTime.now().plusDays(1).withHour(12).withMinute(0).withSecond(0).withNano(0), updated.getEndTime());
    }

    @Test
    @DisplayName("Given overlapping time when update, then IllegalArgumentException")
    void givenOverlappingUpdate_whenUpdateBooking_thenError() {
        BookingRepository bookingRepo = Mockito.mock(BookingRepository.class);
        UserRepository userRepo = Mockito.mock(UserRepository.class);
        RoomRepository roomRepo = Mockito.mock(RoomRepository.class);

        User user = new User(1L, "demo", PasswordUtil.encode("password123"), "demo@example.com", "Demo");
        Room room = new Room(1L, "ROOM-1", "Terem 1", 10, "Epulet 1");
        Booking booking = new Booking(5L, user, room, LocalDateTime.now().plusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0), LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0), BookingStatus.ACTIVE);
        Booking existing = new Booking(6L, user, room, LocalDateTime.now().plusDays(1).withHour(11).withMinute(0).withSecond(0).withNano(0), LocalDateTime.now().plusDays(1).withHour(12).withMinute(0).withSecond(0).withNano(0), BookingStatus.ACTIVE);

        when(userRepo.findByUsername("demo")).thenReturn(Optional.of(user));
        when(roomRepo.findByCode("ROOM-1")).thenReturn(Optional.of(room));
        when(bookingRepo.findById(5L)).thenReturn(Optional.of(booking));
        when(bookingRepo.findByRoomAndStartTimeLessThanAndEndTimeGreaterThanAndStatus(Mockito.eq(room), Mockito.any(), Mockito.any(), Mockito.eq(BookingStatus.ACTIVE)))
                .thenReturn(new java.util.ArrayList<>(Collections.singletonList(existing)));
        when(bookingRepo.save(Mockito.any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookingServiceImpl svc = new BookingServiceImpl(bookingRepo, roomRepo, userRepo);

        BookingDto dto = new BookingDto();
        dto.setRoomCode("ROOM-1");
        dto.setStartTime(LocalDateTime.now().plusDays(1).withHour(11).withMinute(30).withSecond(0).withNano(0).toString());
        dto.setEndTime(LocalDateTime.now().plusDays(1).withHour(13).withMinute(0).withSecond(0).withNano(0).toString());

        assertThrows(IllegalArgumentException.class, () -> svc.update("demo", 5L, dto));
    }
}

