package com.moocode.apifacturas.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record InvoiceLineRequestDTO(
        @NotBlank @Size(max = 350) String description,
        @NotNull @Positive @Digits(integer = 7, fraction = 2)BigDecimal quantity,
        @NotNull @PositiveOrZero @Digits(integer = 10, fraction = 2) BigDecimal unitPrice
        ) {}
