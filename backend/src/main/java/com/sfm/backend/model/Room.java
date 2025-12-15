package com.sfm.backend.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "A terem entitás: információk a teremről")
@Entity
@Table(name = "rooms")
public class Room {

    @Schema(description = "A terem azonosítója", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "A terem kódja (egyedi)", example = "ROOM-1")
    @Column(nullable = false, length = 100, unique = true)
    private String code;

    @Schema(description = "A terem neve", example = "Konferenciaterem A")
    @Column(nullable = false, length = 255)
    private String name;

    @Schema(description = "A férőhelyek száma", example = "20")
    private Integer capacity;

    @Schema(description = "A terem helye", example = "Épület 1, emelet 2")
    @Column(length = 255)
    private String location;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "room_features", joinColumns = @JoinColumn(name = "room_id"))
    @Column(name = "feature")
    private List<String> features = new ArrayList<>();

    public Room() {
    }

    public Room(Long id, String code, String name, Integer capacity, String location) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.capacity = capacity;
        this.location = location;
    }

    public Room(Long id, String code, String name, Integer capacity, String location, List<String> features) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.capacity = capacity;
        this.location = location;
        this.features = features;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<String> getFeatures() {
        return features;
    }

    public void setFeatures(List<String> features) {
        this.features = features;
    }
}
