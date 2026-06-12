package com.traintension.common.exception;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Builder
public record ApiResponseDTO<T>(
        String message,
        T data,
        Object errors,
        Instant timestamp
) {
}
