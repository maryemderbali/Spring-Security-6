package com.ulysseprod.Repositories;

import com.ulysseprod.Entities.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface PermissionRepository extends JpaRepository<Permission, Integer> {

    Permission findPermissionByName (String permissionName);
}
