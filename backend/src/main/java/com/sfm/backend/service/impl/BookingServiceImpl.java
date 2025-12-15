package com.sfm.backend.service.impl;

import com.sfm.backend.dto.BookingDto;
import com.sfm.backend.dto.BookingResponse;
import com.sfm.backend.model.*;
import com.sfm.backend.repository.BookingRepository;
import com.sfm.backend.repository.RoomRepository;
import com.sfm.backend.repository.UserRepository;
import com.sfm.backend.service.BookingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    public BookingServiceImpl(BookingRepository bookingRepository, RoomRepository roomRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Booking book(String username, BookingDto dto) throws IllegalArgumentException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("A felhasználó nem található."));
        Room room = roomRepository.findByCode(dto.getRoomCode()).orElseThrow(() -> new IllegalArgumentException("A terem nem található a rendszerben. Kérjük próbálkozz másik teremmel!"));

        LocalDateTime start;
        LocalDateTime end;
        try {
            start = LocalDateTime.parse(dto.getStartTime());
            end = LocalDateTime.parse(dto.getEndTime());
        } catch (Exception ex) {
            throw new IllegalArgumentException("Érvénytelen időformátum. Használjon ISO-8601 formátumot.");
        }

        if (start.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("A foglalás kezdete nem lehet múltbeli időpont.");
        }

        if (!end.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Nem lehet múltbeli időpontra foglalni.");
        }

        if (!isAlignedTo30Minutes(start) || !isAlignedTo30Minutes(end)) {
            throw new IllegalArgumentException("Az időpontnak 30 perces slotokra kell illeszkednie.");
        }

        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("A záró időpontnak később kell lennie, mint a kezdő időpont.");
        }

        long minutes = Duration.between(start, end).toMinutes();
        if (minutes % 30 != 0) {
            throw new IllegalArgumentException("A foglalás időtartamának 30 perces többszörösnek kell lennie.");
        }

        List<Booking> overlaps = bookingRepository.findByRoomAndStartTimeLessThanAndEndTimeGreaterThanAndStatus(room, end, start, BookingStatus.ACTIVE);
        if (!overlaps.isEmpty()) {
            throw new IllegalArgumentException("Az adott terem a megadott időpontban már foglalt. Kérjük válassz másik időpontot, vagy másik termet!");
        }

        Booking booking = new Booking(null, user, room, start, end, BookingStatus.ACTIVE);
        bookingRepository.save(booking);
        return booking;
    }

    @Override
    @Transactional
    public Booking update(String username, Long bookingId, BookingDto dto) throws IllegalArgumentException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("A felhasználó nem található."));
        Booking existing = bookingRepository.findById(bookingId).orElseThrow(() -> new IllegalArgumentException("A foglalás nem található."));

        if (!existing.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Csak a foglaló felhasználó módosíthatja a foglalást.");
        }

        Room room = roomRepository.findByCode(dto.getRoomCode()).orElseThrow(() -> new IllegalArgumentException("A terem nem található a rendszerben. Kérjük próbálkozz másik teremmel!"));

        LocalDateTime start;
        LocalDateTime end;
        try {
            start = LocalDateTime.parse(dto.getStartTime());
            end = LocalDateTime.parse(dto.getEndTime());
        } catch (Exception ex) {
            throw new IllegalArgumentException("Érvénytelen időformátum. Használjon ISO-8601 formátumot.");
        }

        if (start.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("A foglalás kezdete nem lehet múltbeli időpont.");
        }

        if (!isAlignedTo30Minutes(start) || !isAlignedTo30Minutes(end)) {
            throw new IllegalArgumentException("Az időpontnak 30 perces slotokra kell illeszkednie.");
        }

        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("A záró időpontnak később kell lennie, mint a kezdő időpont.");
        }

        long minutes = Duration.between(start, end).toMinutes();
        if (minutes % 30 != 0) {
            throw new IllegalArgumentException("A foglalás időtartamának 30 perces többszörösnek kell lennie.");
        }

        if (existing.getRoom().getId().equals(room.getId()) && existing.getStartTime().equals(start) && existing.getEndTime().equals(end)) {
            return existing;
        }

        List<Booking> overlaps = bookingRepository.findByRoomAndStartTimeLessThanAndEndTimeGreaterThanAndStatus(room, end, start, BookingStatus.ACTIVE);
        overlaps.removeIf(b -> b.getId().equals(existing.getId()));
        if (!overlaps.isEmpty()) {
            throw new IllegalArgumentException("Az adott terem a megadott időpontban már foglalt. Kérjük válassz másik időpontot, vagy másik termet!");
        }

        existing.setRoom(room);
        existing.setStartTime(start);
        existing.setEndTime(end);
        existing.setStatus(BookingStatus.ACTIVE);
        bookingRepository.save(existing);
        return existing;
    }

    @Override
    @Transactional
    public void cancel(String username, Long bookingId) throws IllegalArgumentException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("A felhasználó nem található."));
        Booking existing = bookingRepository.findById(bookingId).orElseThrow(() -> new IllegalArgumentException("A foglalás nem található."));

        if (!existing.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Csak a foglaló felhasználó törölheti a foglalást.");
        }

        existing.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(existing);
    }

    @Override
    public List<BookingResponse> activeBookingsForUser(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("A felhasználó nem található."));
        List<Booking> active = bookingRepository.findByUserAndStatusAndEndTimeAfter(user, BookingStatus.ACTIVE, LocalDateTime.now());
        return active.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<BookingResponse> historicalBookingsForUser(String username, int limit) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("A felhasználó nem található."));
        List<Booking> past = bookingRepository.findByUserAndEndTimeBeforeOrderByEndTimeDesc(user, LocalDateTime.now());
        if (limit > 0 && past.size() > limit) {
            past = past.subList(0, limit);
        }
        return past.stream().map(this::toResponse).collect(Collectors.toList());
    }

    private BookingResponse toResponse(Booking b) {
        return new BookingResponse(
                b.getId(),
                b.getUser().getUsername(),
                b.getRoom().getCode(),
                b.getRoom().getName(),
                b.getStartTime().toString(),
                b.getEndTime().toString(),
                b.getStatus().name()
        );
    }

    private boolean isAlignedTo30Minutes(LocalDateTime t) {
        int minute = t.getMinute();
        return minute % 30 == 0 && t.getSecond() == 0 && t.getNano() == 0;
    }
}

