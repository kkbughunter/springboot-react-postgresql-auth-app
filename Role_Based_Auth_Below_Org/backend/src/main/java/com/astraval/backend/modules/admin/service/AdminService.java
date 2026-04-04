package com.astraval.backend.modules.admin.service;

import com.astraval.backend.modules.admin.request.AdminUpdateUserRequest;
import com.astraval.backend.modules.admin.request.CreateUserRequest;
import com.astraval.backend.modules.admin.response.AdminUserResponse;

import java.util.List;
import java.util.UUID;

public interface AdminService {

    AdminUserResponse createUser(UUID adminUserId, UUID orgId, CreateUserRequest request);

    List<AdminUserResponse> listUsers(UUID orgId);

    AdminUserResponse getUser(UUID targetUserId, UUID orgId);

    AdminUserResponse updateUser(UUID targetUserId, UUID orgId, AdminUpdateUserRequest request);

    void deleteUser(UUID targetUserId, UUID adminUserId, UUID orgId);
}
