package com.sfm.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Regisztrációs kérés DTO")
public class RegistrationDto {

    @NotBlank(message = "A felhasználónév megadása kötelező.")
    @Size(max = 255, message = "A felhasználónév maximum 255 karakter lehet.")
    @Schema(description = "Felhasználónév", example = "bela")
    private String username;

    @NotBlank(message = "A teljes név megadása kötelező.")
    @Size(max = 255, message = "A teljes név maximum 255 karakter lehet.")
    @Schema(description = "Teljes név", example = "Béla Nagy")
    private String fullName;

    @NotBlank(message = "Az email cím megadása kötelező.")
    @Size(max = 255, message = "Az email cím maximum 255 karakter lehet.")
    @Pattern(regexp = "^(?!@).+@.+\\..+$", message = "Az email cím formátuma nem megfelelő.")
    @Schema(description = "Email cím", example = "bela@example.com")
    private String email;

    @NotBlank(message = "A jelszó megadása kötelező.")
    @Size(min = 8, max = 255, message = "A jelszónak legalább 8 karaktert kell tartalmaznia és maximum 255 lehet.")
    @Schema(description = "Jelszó (legalább 8 karakter)", example = "P@ssw0rd123")
    private String password;

    public RegistrationDto() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
