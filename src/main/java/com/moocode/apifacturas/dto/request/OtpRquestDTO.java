package com.moocode.apifacturas.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record OtpRquestDTO(
        @NotBlank @Email String email
) {}
