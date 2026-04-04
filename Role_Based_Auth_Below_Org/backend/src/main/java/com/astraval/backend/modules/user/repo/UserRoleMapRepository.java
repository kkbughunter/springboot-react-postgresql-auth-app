package com.astraval.backend.modules.user.repo;

import com.astraval.backend.modules.user.entity.UserRoleMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRoleMapRepository extends JpaRepository<UserRoleMap, Long> {

    @Query("SELECT urm FROM UserRoleMap urm JOIN FETCH urm.role WHERE urm.user.userId = :userId AND urm.org.orgId = :orgId AND urm.isActive = true")
    List<UserRoleMap> findActiveByUserIdAndOrgId(@Param("userId") UUID userId, @Param("orgId") UUID orgId);

    @Query("SELECT urm FROM UserRoleMap urm JOIN FETCH urm.role WHERE urm.user.userId = :userId AND urm.isActive = true")
    List<UserRoleMap> findActiveByUserId(@Param("userId") UUID userId);

    boolean existsByUser_UserIdAndRole_RoleIdAndOrg_OrgId(UUID userId, int roleId, UUID orgId);
}
