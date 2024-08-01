package com.ulysseprod.Services;


import com.ulysseprod.Entities.User;
import com.ulysseprod.Repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService{


    UserRepository userRepository;
    @Override
    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    @Override
    public void addUser(User user) {
        
    }

    @Override
    public User deleteUser(Integer id) {
        User user = userRepository.findById(id).orElse(null);
        userRepository.delete(user);
        return user;
    }


}
