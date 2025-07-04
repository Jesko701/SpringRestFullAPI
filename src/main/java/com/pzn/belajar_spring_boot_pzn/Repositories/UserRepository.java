package com.pzn.belajar_spring_boot_pzn.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pzn.belajar_spring_boot_pzn.Entity.User;

public interface UserRepository extends JpaRepository<User,String> {

    Optional<User> findFirstByToken(String token);
}
