package com.moocode.apifacturas.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CustomerRequestDTO(
        @NotBlank @Size(max = 100) String firstName,
        @NotBlank @Size(max = 100) String paternalSurname,
        @Size(max = 100) String maternalSurname,
        @NotBlank @Email @Size(max = 20) String email,
        @Size(max = 20) String phone
) {}
