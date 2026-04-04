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

    @PostMapping
    public ResponseEntity<ApiResponse<AdminUserResponse>> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        UUID adminUserId = securityUtil.getCurrentUserId();
        AdminUserResponse response = adminService.createUser(adminUserId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseFactory.success("User created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminUserResponse>>> listUsers() {
        List<AdminUserResponse> users = adminService.listUsers();
        return ResponseEntity.ok(ApiResponseFactory.success("Users fetched", users));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<AdminUserResponse>> getUser(
            @PathVariable UUID userId) {
        AdminUserResponse response = adminService.getUser(userId);
        return ResponseEntity.ok(ApiResponseFactory.success("User fetched", response));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<AdminUserResponse>> updateUser(
            @PathVariable UUID userId,
            @Valid @RequestBody AdminUpdateUserRequest request) {
        AdminUserResponse response = adminService.updateUser(userId, request);
        return ResponseEntity.ok(ApiResponseFactory.success("User updated successfully", response));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable UUID userId) {
        UUID adminUserId = securityUtil.getCurrentUserId();
        adminService.deleteUser(userId, adminUserId);
        return ResponseEntity.ok(ApiResponseFactory.success("User deleted successfully", null));
    }
}
