package com.sfm.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Profil módosítási DTO")
public class ModifyContactInfoDto {

    @Schema(description = "Jelenlegi jelszó (szükséges minden módosításhoz)", example = "password123", required = true)
    @NotBlank(message = "A jelenlegi jelszó megadása kötelező.")
    private String currentPassword;

    @Schema(description = "Új teljes név (opcionális)", example = "Béla Nagy")
    private String fullName;

    @Schema(description = "Új email cím (opcionális)", example = "ujbela@example.com")
    @Email(message = "Az email formátuma nem megfelelő.")
    private String email;

    @Schema(description = "Új jelszó (opcionális)", example = "newpassword123")
    private String newPassword;

    public ModifyContactInfoDto() {
    }

    public ModifyContactInfoDto(String currentPassword, String fullName, String email, String newPassword) {
        this.currentPassword = currentPassword;
        this.fullName = fullName;
        this.email = email;
        this.newPassword = newPassword;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
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

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
