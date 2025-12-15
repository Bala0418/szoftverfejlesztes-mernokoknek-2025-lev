package com.sfm.backend.service.impl;

import com.sfm.backend.model.Room;
import com.sfm.backend.repository.RoomRepository;
import com.sfm.backend.service.RoomService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    public RoomServiceImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public List<Room> listRooms() {
        return roomRepository.findAll();
    }

    @Override
    public Optional<Room> findByCode(String code) {
        return roomRepository.findByCode(code);
    }
}

