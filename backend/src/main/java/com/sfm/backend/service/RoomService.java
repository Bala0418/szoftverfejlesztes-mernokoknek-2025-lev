package com.sfm.backend.service;

import com.sfm.backend.model.Room;

import java.util.List;
import java.util.Optional;

public interface RoomService {
    List<Room> listRooms();
    Optional<Room> findByCode(String code);
}

