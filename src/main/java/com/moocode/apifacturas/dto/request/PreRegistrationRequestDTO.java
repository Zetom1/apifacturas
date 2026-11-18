package com.moocode.apifacturas.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PreRegistrationRequestDTO(
    @NotBlank @Size(max = 100) String firstName,
    @NotBlank @Size(max = 100) String paternalSurname,
    @Size(max = 100) String maternalSurname,
    @NotBlank @Email @Size(max = 254) String email,
    @Size(max = 20) String phone,
    @NotBlank @Size(min = 8, max = 72) String Password
) {}
