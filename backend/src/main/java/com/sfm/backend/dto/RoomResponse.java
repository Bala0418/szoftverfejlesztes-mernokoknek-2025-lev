package com.sfm.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Termek válasz DTO")
public class RoomResponse {

    @Schema(description = "Terem azonosító", example = "1")
    private Long id;

    @Schema(description = "Terem kód", example = "ROOM-101")
    private String code;

    @Schema(description = "Terem neve", example = "Gépterem 101")
    private String name;

    @Schema(description = "Férőhelyek száma", example = "24")
    private Integer capacity;

    @Schema(description = "Helyszín", example = "Informatikai épület – 1. emelet")
    private String location;

    @Schema(description = "Felszereltség lista")
    private List<String> features;

    public RoomResponse() {}

    public RoomResponse(Long id, String code, String name, Integer capacity, String location, List<String> features) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.capacity = capacity;
        this.location = location;
        this.features = features;
    }

    // getters / setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public List<String> getFeatures() { return features; }
    public void setFeatures(List<String> features) { this.features = features; }
}

