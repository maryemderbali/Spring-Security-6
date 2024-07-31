package com.ulysseprod.Services;


import com.ulysseprod.Entities.User;

import java.util.List;

public interface UserService {

     public List<User> getAllUser() ;
    public void addUser(User user);

    public User deleteUser(Integer id);

}
