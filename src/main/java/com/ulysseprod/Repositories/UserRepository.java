package com.ulysseprod.Repositories;

import com.ulysseprod.Entities.Role;
import com.ulysseprod.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User,Integer> {

   Optional<User> findByUsername(String username);
   Optional<User> findByEmail(String email);
   Optional <User> findById(Integer id);
   User findUserByEmail(String email);
   List<User> findByRolesContaining(Role role);

}
