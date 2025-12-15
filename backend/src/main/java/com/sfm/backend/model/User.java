package com.sfm.backend.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

@Schema(description = "Felhasználó entitás (jelenleg csak alapadatok)")
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})
public class User {

    @Schema(description = "Felhasználó azonosítója", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Felhasználónév", example = "bela")
    @Column(nullable = false, length = 255)
    private String username;

    @Schema(description = "Jelszó hash (nem a nyers jelszó)", example = "<hash>")
    @Column(nullable = false, length = 255)
    private String passwordHash;

    @Schema(description = "Email cím", example = "bela@example.com")
    @Column(nullable = false, length = 255)
    private String email;

    @Schema(description = "Teljes név", example = "Béla Nagy")
    @Column(length = 255)
    private String fullName;

    public User() {
    }

    public User(Long id, String username, String passwordHash, String email, String fullName) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.fullName = fullName;
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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
