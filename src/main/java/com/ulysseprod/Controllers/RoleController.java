package com.ulysseprod.Controllers;

import com.ulysseprod.Entities.Role;
import com.ulysseprod.Services.RoleServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/role")
public class RoleController {
    @Autowired
    RoleServiceImpl roleService;

    @PostMapping("/addRole/{RolesName}")
        @PreAuthorize("hasAuthority('CREATE_ROLE')")
    public void addRole(@PathVariable("RolesName") String roleName) {
        roleService.addRole(roleName);
    }


    @PreAuthorize("hasAuthority('DELETE_ROLE')")
    @DeleteMapping("/{name}")
    public void deleteRole(@PathVariable("name") String roleName) {
        roleService.deleteRole(roleName);
    }

    @GetMapping("/all")
    public List<Role> getAllRoles() {
        return roleService.getAllRoles();
    }


    @PreAuthorize("hasAnyAuthority('CREATE_PERMISSION','CREATE_ROLE')")
    @PostMapping("/AssignPermission/{rolename}/{permission}")
    public void assignPermissionToRole(@PathVariable("rolename") String roleName,
                                       @PathVariable("permission")String permissionName)
    {
        roleService.assignPermissionToRole(roleName, permissionName);
    }

    @PreAuthorize("hasAnyAuthority('DELETE_PERMISSION','EDIT_ROLE')")
    @DeleteMapping("/RemovePermission/{roleName}/{permissionName}")

    public void removePermissionFromRole(@PathVariable("roleName") String roleName
            , @PathVariable("permissionName") String permissionName)
    {
        roleService.removePermissionFromRole(roleName, permissionName);
    }
}
