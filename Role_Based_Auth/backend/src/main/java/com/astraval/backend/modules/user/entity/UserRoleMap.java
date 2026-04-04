package com.astraval.backend.modules.user.entity;

import com.astraval.backend.common.BaseAuditEntity;
import com.astraval.backend.modules.role.entity.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "user_role_map",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_user_role",
        columnNames = {"user_id", "role_id"}
    )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleMap extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
}
