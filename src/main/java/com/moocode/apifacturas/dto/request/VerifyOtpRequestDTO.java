package com.moocode.apifacturas.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VerifyOtpRequestDTO(
        @NotBlank @Email String email,
        @NotBlank @Pattern(regexp = "[exp 0-9]{6}") String code
) {}
