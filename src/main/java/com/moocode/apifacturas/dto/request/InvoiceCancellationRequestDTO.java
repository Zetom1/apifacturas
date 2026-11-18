package com.moocode.apifacturas.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record InvoiceCancellationRequestDTO(@NotBlank @Size(max = 100) String reason) {}
