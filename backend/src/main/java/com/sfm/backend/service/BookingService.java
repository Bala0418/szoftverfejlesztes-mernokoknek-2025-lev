package com.sfm.backend.service;

import com.sfm.backend.dto.BookingDto;
import com.sfm.backend.dto.BookingResponse;
import com.sfm.backend.model.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingService {
    Booking book(String username, BookingDto dto) throws IllegalArgumentException;
    Booking update(String username, Long bookingId, BookingDto dto) throws IllegalArgumentException;
    void cancel(String username, Long bookingId) throws IllegalArgumentException;
    List<BookingResponse> activeBookingsForUser(String username);
    List<BookingResponse> historicalBookingsForUser(String username, int limit);
}
