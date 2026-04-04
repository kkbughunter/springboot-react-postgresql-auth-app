package com.astraval.backend.modules.user.entity;

import com.astraval.backend.common.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "user_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDetail extends BaseAuditEntity {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(name = "gender", length = 10)
    private String gender;

    @Column(name = "email", nullable = false, length = 150, unique = true)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;
}
