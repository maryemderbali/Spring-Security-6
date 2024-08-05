package com.ulysseprod.Services;


import com.ulysseprod.Entities.Role;
import com.ulysseprod.Entities.User;
import com.ulysseprod.Repositories.RoleRepository;
import com.ulysseprod.Repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService{


    UserRepository userRepository;
    RoleRepository roleRepository;
    PasswordEncoder passwordEncoder;
    @Override
    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    @Override
    public void createUser(User user, String roleName) {
        Optional<Role> role = roleRepository.findByName(roleName);

        if (role.isEmpty()) {
            throw new RuntimeException("Role not found");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true);
        user.setRoles(Collections.singletonList(role.get()));
        userRepository.save(user);


    }

    @Override
    public User updateUser(User user) {
        return null;
    }

    @Override
    public  void deleteUser(Integer id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        userRepository.delete(userOptional.get());
    }


    public String  blockUser(Integer id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (!user.isBlocked()) {
                user.setBlocked(true);
                userRepository.save(user);
                return "User with ID " + id + " has been blocked successfully.";
            } else {
                return "User with ID " + id + " is already blocked.";
            }
        } else {
            return "User with ID " + id + " not found.";
        }
    }
}
