package com.moocode.apifacturas.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CustomerProfileUpdateDTO(
        @NotBlank @Size(max = 100) String firstName,
        @NotBlank @Size(max = 100) String paternalSurname,
        @Size(max = 100) String maternalSurname,
        @Size(max = 20) String phone
) {}
