package com.ulysseprod.Services;

import com.ulysseprod.Entities.Role;
import com.ulysseprod.Repositories.RoleRepository;
import com.ulysseprod.Repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class RoleServiceImpl implements RoleService {
    RoleRepository roleRepository;
    UserRepository userRepository;

    @Override
    public void addRole(String roleName) {
        Role role=new Role();
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


}
