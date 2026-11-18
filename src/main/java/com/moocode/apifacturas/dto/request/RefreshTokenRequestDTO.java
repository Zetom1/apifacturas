package com.moocode.apifacturas.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequestDTO(
    @NotBlank String refreshToken
) {}
