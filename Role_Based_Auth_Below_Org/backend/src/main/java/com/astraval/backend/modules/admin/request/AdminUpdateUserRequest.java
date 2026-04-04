package com.astraval.backend.modules.admin.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdminUpdateUserRequest {

    @NotBlank(message = "Full name is required")
    @Size(max = 150, message = "Full name must not exceed 150 characters")
    private String fullName;

    @Size(max = 10, message = "Gender must not exceed 10 characters")
    private String gender;

    @Size(max = 20, message = "Phone must not exceed 20 characters")
    private String phone;

    /** Set to false to deactivate the user */
    private Boolean isActive;
}
