package com.sfm.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Egyszerű üzenet választ adó DTO")
public class SimpleMessageResponse {

    @Schema(description = "Válasz üzenet", example = "Sikeres regisztráció!")
    private String message;

    public SimpleMessageResponse() {
    }

    public SimpleMessageResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

