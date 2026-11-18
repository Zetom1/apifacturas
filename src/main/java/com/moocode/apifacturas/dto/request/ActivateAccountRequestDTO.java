package com.moocode.apifacturas.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ActivateAccountRequestDTO(@NotBlank @Email String email) {}
