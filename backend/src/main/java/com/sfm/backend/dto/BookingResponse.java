package com.sfm.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Foglalás válasz DTO")
public class BookingResponse {

    @Schema(description = "Foglalás azonosító", example = "1")
    private Long id;

    @Schema(description = "Felhasználó neve (username)", example = "bela")
    private String username;

    @Schema(description = "Terem kód", example = "ROOM-1")
    private String roomCode;

    @Schema(description = "Terem neve", example = "Konferenciaterem A")
    private String roomName;

    @Schema(description = "Kezdő idő (ISO-8601)", example = "2025-12-18T10:00:00")
    private String startTime;

    @Schema(description = "Vég idő (ISO-8601)", example = "2025-12-18T10:30:00")
    private String endTime;

    @Schema(description = "Foglalás státusza", example = "ACTIVE")
    private String status;

    public BookingResponse() {
    }

    public BookingResponse(Long id, String username, String roomCode, String roomName, String startTime, String endTime, String status) {
        this.id = id;
        this.username = username;
        this.roomCode = roomCode;
        this.roomName = roomName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

