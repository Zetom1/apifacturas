package com.moocode.apifacturas.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record InvoiceRequestDTO(
        @NotBlank String customerCode,
        @NotEmpty @Size(max = 100) List<@Valid InvoiceLineRequestDTO> concepts
) {}
