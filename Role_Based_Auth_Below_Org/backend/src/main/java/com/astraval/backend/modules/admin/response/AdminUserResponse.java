package com.astraval.backend.modules.admin.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserResponse {

    private String userId;
    private String orgId;
    private String email;
    private String fullName;
    private String gender;
    private String phone;
    private Boolean isActive;
    private String parentUserId;
    private List<String> roles;
}
