package com.astraval.backend.modules.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Organization name is required")
    @Size(max = 150, message = "Organization name must not exceed 150 characters")
    private String orgName;

    @NotBlank(message = "Organization code is required")
    @Size(min = 3, max = 100, message = "Organization code must be 3–100 characters")
    @Pattern(regexp = "^[a-z0-9_-]+$", message = "Organization code must be lowercase alphanumeric, hyphens or underscores only")
    private String orgCode;

    @NotBlank(message = "Full name is required")
    @Size(max = 150, message = "Full name must not exceed 150 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @Size(max = 20, message = "Phone must not exceed 20 characters")
    private String phone;
}
