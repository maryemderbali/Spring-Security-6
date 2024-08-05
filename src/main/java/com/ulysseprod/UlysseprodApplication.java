package com.ulysseprod;

import com.ulysseprod.Entities.Permission;
import com.ulysseprod.Entities.Role;
import com.ulysseprod.Entities.User;
import com.ulysseprod.Repositories.PermissionRepository;
import com.ulysseprod.Repositories.RoleRepository;
import com.ulysseprod.Repositories.UserRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class UlysseprodApplication {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PermissionRepository permissionRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(UlysseprodApplication.class, args);
    }


    @Bean
    InitializingBean sendDatabase() {
        return this::configPermissions;
    }


    public  void checkOrCreatePermissions(String entity)
    {

        String[] actionsList = {"READ", "CREATE", "DELETE", "EDIT"};

        for(String action : actionsList){
            String permissionName  = action+"_"+entity.toUpperCase();
            Permission permission = permissionRepository.findPermissionByName(permissionName);
            if (permission == null) {
                permissionRepository.saveAndFlush(Permission.builder().name(permissionName).build());
            }
        }

    }
    public  void configPermissions(){
        List<String> entities = new ArrayList<>();
        entities.add("User");
        entities.add("Role");
        entities.add("Token");
        entities.add("Permission");
        for(String entity : entities){
            checkOrCreatePermissions(entity);
        }
        createSuperUser();

    }

    public void createSuperUser(){
        User u = userRepository.findByEmail("super@app.com").orElse(null);
        if(u == null){
            List<Permission> allPermissions = permissionRepository.findAll();
            Role newRole = new Role();
            newRole.setName("SUPER_ADMIN");
            newRole.setPermissions(allPermissions);
            roleRepository.save(newRole);

            var superuser = User.builder()
                    .email("super@app.com")
                    .username("super")
                    .password(passwordEncoder.encode("12345678super"))
                    .accountLocked(false)
                    .enabled(true)
                    .roles(List.of(newRole))
                    .build();
            userRepository.save(superuser);

        }
    }


}
