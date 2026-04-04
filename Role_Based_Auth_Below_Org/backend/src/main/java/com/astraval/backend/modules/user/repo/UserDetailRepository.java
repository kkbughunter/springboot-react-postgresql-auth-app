package com.astraval.backend.modules.user.repo;

import com.astraval.backend.modules.user.entity.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserDetailRepository extends JpaRepository<UserDetail, UUID> {

    Optional<UserDetail> findByUserId(UUID userId);

    Optional<UserDetail> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT COUNT(ud) > 0 FROM UserDetail ud WHERE ud.email = :email AND ud.userId != :userId")
    boolean existsByEmailAndUserIdNot(@Param("email") String email, @Param("userId") UUID userId);
}
