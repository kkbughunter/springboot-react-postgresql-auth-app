package com.astraval.backend.modules.role.entity;

import com.astraval.backend.common.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role extends BaseAuditEntity {

    @Id
    @Column(name = "role_id")
    private Integer roleId;

    @Column(name = "role_name", unique = true, nullable = false, length = 50)
    private String roleName;

    @Column(name = "landing_url", length = 255)
    private String landingUrl;

    @Column(name = "is_system")
    private Boolean isSystem = false;

    public static final int ADMIN_ID = 1;
    public static final int USER_ID  = 2;

    public static final String ADMIN = "ADMIN";
    public static final String USER  = "USER";
}
