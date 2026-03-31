package com.astraval.backend.modules.organization.repo;

import com.astraval.backend.modules.organization.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, UUID> {

    Optional<Organization> findByOrgCode(String orgCode);

    Optional<Organization> findByDomainUrl(String domainUrl);

    boolean existsByOrgCode(String orgCode);
}
