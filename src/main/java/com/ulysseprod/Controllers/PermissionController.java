package com.ulysseprod.Controllers;

import com.ulysseprod.Entities.Permission;
import com.ulysseprod.Services.PermissionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("api/Permission")
@RestController
public class PermissionController {

    PermissionService permissionService;

    @PreAuthorize("hasAuthority('READ_PERMISSION')")
    @GetMapping
    public List<Permission> GetAll() {
        return permissionService.GetAll();
    }

    @PreAuthorize("hasAuthority('CREATE_PERMISSION')")
    @PostMapping("/addPermission")
    public List<Permission> CreatePermission(@RequestBody Permission permission) {
        return permissionService.CreatePermission(permission);
    }


}
