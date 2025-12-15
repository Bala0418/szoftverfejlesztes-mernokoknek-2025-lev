package com.sfm.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Foglalási kérés DTO")
public class BookingDto {

    @NotBlank(message = "A teremkód megadása kötelező.")
    @Size(max = 255, message = "A teremkód maximum 255 karakter lehet.")
    @Schema(description = "A terem kódja", example = "ROOM-1")
    private String roomCode;

    @NotBlank(message = "A foglalás kezdőidőpontja kötelező.")
    @Schema(description = "Kezdő idő (ISO-8601)", example = "2025-12-18T10:00:00")
    private String startTime;

    @NotBlank(message = "A foglalás végidőpontja kötelező.")
    @Schema(description = "Vég idő (ISO-8601)", example = "2025-12-18T10:30:00")
    private String endTime;

    @Size(max = 255, message = "A foglalás címe maximum 255 karakter lehet.")
    @Schema(description = "Foglalás címe", example = "Team meeting")
    private String title;

    public BookingDto() {
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
