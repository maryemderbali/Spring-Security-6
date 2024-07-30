package com.ulysseprod.Repositories;

import com.ulysseprod.DTO.RoleName;
import com.ulysseprod.Entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface  RoleRepository extends JpaRepository<Role,Integer> {
    Optional<Role> findByName(RoleName roleName );


}
