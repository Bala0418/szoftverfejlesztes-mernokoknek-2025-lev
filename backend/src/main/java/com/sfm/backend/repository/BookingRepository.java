package com.sfm.backend.repository;

import com.sfm.backend.model.Booking;
import com.sfm.backend.model.BookingStatus;
import com.sfm.backend.model.Room;
import com.sfm.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByRoomAndStartTimeLessThanAndEndTimeGreaterThanAndStatus(Room room, LocalDateTime end, LocalDateTime start, BookingStatus status);

    List<Booking> findByUserAndStatus(User user, BookingStatus status);

    List<Booking> findByUserAndEndTimeBeforeOrderByEndTimeDesc(User user, LocalDateTime before);

    List<Booking> findByUserAndStatusAndEndTimeAfter(User user, BookingStatus status, LocalDateTime after);
}
