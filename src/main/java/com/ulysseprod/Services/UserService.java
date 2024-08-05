package com.ulysseprod.Services;


import com.ulysseprod.Entities.User;

import java.util.List;

public interface UserService {
     public List<User> getAllUser() ;
    public void createUser(User user, String roleName);
    public User updateUser(User user);
    public void deleteUser(Integer id);
}
