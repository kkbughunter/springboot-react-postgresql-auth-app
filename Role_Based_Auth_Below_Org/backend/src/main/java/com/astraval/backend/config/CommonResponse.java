package com.astraval.backend.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Simple response bean used by filters to write JSON error responses
 * before the request reaches the controller layer.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonResponse {
    private boolean success;
    private String message;
}
