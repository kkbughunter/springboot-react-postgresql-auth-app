package com.astraval.backend.modules.admin;

import com.astraval.backend.common.util.ApiResponse;
import com.astraval.backend.common.util.ApiResponseFactory;
import com.astraval.backend.common.util.SecurityUtil;
import com.astraval.backend.modules.admin.request.AdminUpdateUserRequest;
import com.astraval.backend.modules.admin.request.CreateUserRequest;
import com.astraval.backend.modules.admin.response.AdminUserResponse;
import com.astraval.backend.modules.admin.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final SecurityUtil securityUtil;

    /** Create a new user within the admin's organization */
    @PostMapping
    public ResponseEntity<ApiResponse<AdminUserResponse>> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        UUID adminUserId = securityUtil.getCurrentUserId();
        UUID orgId = securityUtil.getCurrentOrgId();
        AdminUserResponse response = adminService.createUser(adminUserId, orgId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseFactory.success("User created successfully", response));
    }

    /** List all users in the admin's organization */
    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminUserResponse>>> listUsers() {
        UUID orgId = securityUtil.getCurrentOrgId();
        List<AdminUserResponse> users = adminService.listUsers(orgId);
        return ResponseEntity.ok(ApiResponseFactory.success("Users fetched", users));
    }

    /** Get a specific user by ID (must belong to admin's org) */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<AdminUserResponse>> getUser(
            @PathVariable UUID userId) {
        UUID orgId = securityUtil.getCurrentOrgId();
        AdminUserResponse response = adminService.getUser(userId, orgId);
        return ResponseEntity.ok(ApiResponseFactory.success("User fetched", response));
    }

    /** Update a user's profile details or active status */
    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<AdminUserResponse>> updateUser(
            @PathVariable UUID userId,
            @Valid @RequestBody AdminUpdateUserRequest request) {
        UUID orgId = securityUtil.getCurrentOrgId();
        AdminUserResponse response = adminService.updateUser(userId, orgId, request);
        return ResponseEntity.ok(ApiResponseFactory.success("User updated successfully", response));
    }

    /** Soft-delete a user (sets isActive = false) */
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable UUID userId) {
        UUID adminUserId = securityUtil.getCurrentUserId();
        UUID orgId = securityUtil.getCurrentOrgId();
        adminService.deleteUser(userId, adminUserId, orgId);
        return ResponseEntity.ok(ApiResponseFactory.success("User deleted successfully", null));
    }
}
