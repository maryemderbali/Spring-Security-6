package com.ulysseprod.Controllers;

import com.ulysseprod.Entities.User;
import com.ulysseprod.Services.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    UserServiceImpl userService;

    @PreAuthorize("hasAuthority('READ_USER')")
    @GetMapping
    public List<User> getAllUser() {
        return userService.getAllUser();
    }

    @PreAuthorize("hasAnyAuthority('CREATE_USER','CREATE_ROLE')")
    @PostMapping("/create")
    public ResponseEntity<String> createUser(@RequestBody User user, @RequestParam String roleName) {
        try {
            userService.createUser(user, roleName);
            return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully with role: " + roleName);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PreAuthorize("hasAuthority('EDIT_USER')")
    @PutMapping("/update")
    public User updateUser(User user) {
        return userService.updateUser(user);
    }

    @PreAuthorize("hasAuthority('DELETE_USER')")
    @DeleteMapping("/delete/{id}")
    public void deleteUser(@PathVariable("id") Integer id) {
            userService.deleteUser(id);
    }

    @PreAuthorize("hasAnyAuthority('DELETE_USER','CREATE_USER','EDIT_USER')")
    @PutMapping("/block/{id}")
    public ResponseEntity<String> blockUser(@PathVariable("id") Integer id) {
        String result = userService.blockUser(id);
        if (result.contains("not found")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
        return ResponseEntity.ok(result);
    }
    @PreAuthorize("hasAnyAuthority('DELETE_USER','CREATE_USER','EDIT_USER')")
    @PutMapping("/unblock/{id}")
    public ResponseEntity<String> unblockUser(@PathVariable("id") Integer id) {
        String result = userService.unblockUser(id);
        if (result.contains("not found")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
        return ResponseEntity.ok(result);
    }



}
