package org.example.version2_xpresbank.Repository;

import org.example.version2_xpresbank.Entity.Enums.RoleType;
import org.example.version2_xpresbank.Entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleType name);
}
