package com.sfm.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Bejelentkezés DTO")
public class LoginDto {

    @NotBlank(message = "A felhasználónév vagy email megadása kötelező.")
    @Size(max = 255, message = "A felhasználónév/email maximum 255 karakter lehet.")
    @Schema(description = "Felhasználónév vagy email", example = "bela")
    private String usernameOrEmail;

    @NotBlank(message = "A jelszó megadása kötelező.")
    @Size(min = 8, max = 255, message = "A jelszónak legalább 8 karaktert kell tartalmaznia.")
    @Schema(description = "Jelszó", example = "password123")
    private String password;

    public LoginDto() {
    }

    public String getUsernameOrEmail() {
        return usernameOrEmail;
    }

    public void setUsernameOrEmail(String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
