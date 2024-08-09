package com.ulysseprod.Services;

import com.ulysseprod.Entities.Permission;
import com.ulysseprod.Entities.Role;
import com.ulysseprod.Repositories.PermissionRepository;
import com.ulysseprod.Repositories.RoleRepository;
import com.ulysseprod.Repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RoleServiceImpl implements RoleService {
    RoleRepository roleRepository;
    UserRepository userRepository;
    PermissionRepository permissionRepository;

    @Override
    public void addRole(String roleName) {
        Role role = new Role();
        role.setName(roleName);
        roleRepository.save(role);
    }

    @Override
    public void deleteRole(String roleName) {
        Role roleToDelete = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        roleRepository.delete(roleToDelete);
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }


    public void assignPermissionToRole(String roleName, String permissionName) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        Permission permission = permissionRepository.findPermissionByName(permissionName);

        role.getPermissions().add(permission);

        roleRepository.save(role);
    }

    public void removePermissionFromRole(String roleName, String permissionName) {
        Optional<Role> roleOptional = roleRepository.findByName(roleName);
        if (roleOptional.isEmpty()) {
            throw new RuntimeException("Role not found");
        }

        Role role = roleOptional.get();
        Permission permission = permissionRepository.findPermissionByName(permissionName);

        role.getPermissions().remove(permission);
        roleRepository.save(role);
    }

    public List<Permission> getPermissionsByRoleName(String roleName) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
        return role.getPermissions();
    }
}
