package com.ulysseprod.Services;

import com.ulysseprod.Entities.Permission;
import com.ulysseprod.Repositories.PermissionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PermissionService {

    PermissionRepository permissionRepository;

    public List<Permission> GetAll(){
        return permissionRepository.findAll();
    }

    public List<Permission> CreatePermission (Permission permission){

            permissionRepository.save(permission);
            return GetAll();
        }


}
