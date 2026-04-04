package com.astraval.backend.common;

import com.astraval.backend.modules.role.entity.Role;
import com.astraval.backend.modules.role.repo.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedRole(Role.ADMIN_ID, Role.ADMIN, "/admin/dashboard", true);
        seedRole(Role.USER_ID, Role.USER, "/dashboard", true);
    }

    private void seedRole(int id, String name, String landingUrl, boolean isSystem) {
        if (roleRepository.existsById(id)) return;
        Role role = new Role();
        role.setRoleId(id);
        role.setRoleName(name);
        role.setLandingUrl(landingUrl);
        role.setIsSystem(isSystem);
        roleRepository.save(role);
        log.info("Seeded role: {}", name);
    }
}
