package com.moocode.apifacturas.dto.response;

public record AuthenticatedProfileResponseDTO(
        String userCode,
        String fullName,
        String email,
        String role
) {
}
