package com.pzn.belajar_spring_boot_pzn.Service;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.pzn.belajar_spring_boot_pzn.Entity.User;
import com.pzn.belajar_spring_boot_pzn.Repositories.UserRepository;
import com.pzn.belajar_spring_boot_pzn.Security.BCrypt;
import com.pzn.belajar_spring_boot_pzn.model.RegisterUserRequest;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Validator validator;

    @Async
    @Transactional
    public CompletableFuture<Void> register(RegisterUserRequest request) {
        return CompletableFuture.runAsync(() -> {
            Set<ConstraintViolation<RegisterUserRequest>> constraintViolations = validator.validate(request);
            if (constraintViolations.size() != 0){
                // error
                throw new ConstraintViolationException(constraintViolations);
            }
    
            if (userRepository.existsById(request.getUsername())){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Username already registered");
            }
    
            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
            user.setName(request.getName());
    
            userRepository.save(user);
        });
    }
}
