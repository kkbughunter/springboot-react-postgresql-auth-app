package com.astraval.backend.modules.user.repo;

import com.astraval.backend.modules.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    @Query("SELECT u FROM User u WHERE u.org.orgId = :orgId AND u.isActive = true")
    List<User> findAllByOrgId(@Param("orgId") UUID orgId);

    @Query("SELECT u FROM User u WHERE u.userId = :userId AND u.org.orgId = :orgId")
    Optional<User> findByUserIdAndOrgId(@Param("userId") UUID userId, @Param("orgId") UUID orgId);
}
