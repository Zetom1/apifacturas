package com.moocode.apifacturas.dto.response;

import java.time.Instant;

public record CustomerResponseDTO(
        String CustomerCode,
        String firstName,
        String PaternalSurname,
        String maternalSurname,
        String email,
        String phone,
        boolean active,
        Instant createdAt
) {}
