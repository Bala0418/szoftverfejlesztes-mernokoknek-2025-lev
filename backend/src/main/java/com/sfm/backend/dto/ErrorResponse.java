package com.sfm.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Hibaválaszok egységes formátuma")
public class ErrorResponse {

    @Schema(description = "Rövid hiba címke", example = "Érvényességi hiba")
    private String errorMessage;

    @Schema(description = "Részletes üzenet: lehet string vagy mezőspecifikus objektum (map)")
    private Object message;

    public ErrorResponse() {
    }

    public ErrorResponse(String errorMessage, Object message) {
        this.errorMessage = errorMessage;
        this.message = message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }
}

