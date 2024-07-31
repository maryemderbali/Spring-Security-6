package com.ulysseprod.Services;

import com.ulysseprod.Entities.Role;

import java.util.List;

public interface RoleService {
    void addRole(String roleName);
    void deleteRole(String roleName);
    List<Role> getAllRoles();

}
