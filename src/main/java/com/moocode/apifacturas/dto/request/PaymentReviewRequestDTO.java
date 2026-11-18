package com.moocode.apifacturas.dto.request;

import jakarta.validation.constraints.Size;

public record PaymentReviewRequestDTO(
        @Size(max = 500) String comment
) {}
