package com.moocode.apifacturas.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record PaymentRequestDTO(
        @NotNull @DecimalMin(value = "0.01")BigDecimal amount,
        @NotBlank @Size(max = 30) String paymentMethod,
        @Size(max = 100) String reference,
        @Size(max = 500) String observations
        ) {}
