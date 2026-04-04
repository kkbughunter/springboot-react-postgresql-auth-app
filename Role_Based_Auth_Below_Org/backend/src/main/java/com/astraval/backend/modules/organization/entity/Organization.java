package com.astraval.backend.modules.organization.entity;

import com.astraval.backend.common.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "organizations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Organization extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "org_id", updatable = false, nullable = false)
    private UUID orgId;

    @Column(name = "org_name", nullable = false, length = 150)
    private String orgName;

    @Column(name = "org_code", unique = true, nullable = false, length = 100)
    private String orgCode;

    @Column(name = "domain_url", unique = true, length = 255)
    private String domainUrl;
}
