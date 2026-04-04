package com.astraval.backend.modules.admin.service;

import com.astraval.backend.modules.admin.request.AdminUpdateUserRequest;
import com.astraval.backend.modules.admin.request.CreateUserRequest;
import com.astraval.backend.modules.admin.response.AdminUserResponse;

import java.util.List;
import java.util.UUID;

public interface AdminService {

    AdminUserResponse createUser(UUID adminUserId, CreateUserRequest request);

    List<AdminUserResponse> listUsers();

    AdminUserResponse getUser(UUID targetUserId);

    AdminUserResponse updateUser(UUID targetUserId, AdminUpdateUserRequest request);

    void deleteUser(UUID targetUserId, UUID adminUserId);
}
