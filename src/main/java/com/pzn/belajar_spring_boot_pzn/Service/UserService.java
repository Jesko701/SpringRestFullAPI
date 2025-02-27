package com.pzn.belajar_spring_boot_pzn.Service;

import java.util.Objects;
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
import com.pzn.belajar_spring_boot_pzn.model.UpdateUserRequest;
import com.pzn.belajar_spring_boot_pzn.model.UserResponse;

import jakarta.transaction.Transactional;
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidationService validationService;

    // * Thread Async
    @Async
    public CompletableFuture<Void> register(RegisterUserRequest request) {
        return CompletableFuture.runAsync(() -> {
            saveUser(request); // Calls the transactional method
        });
    }

    // * Each thread has their own transactional method
    @Transactional // Ensures database operations are properly managed
    public void saveUser(RegisterUserRequest request) {
        validationService.validate(request);

        if (userRepository.existsById(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already registered");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        user.setName(request.getName());

        userRepository.save(user);
    }

    public UserResponse get(User user){
        return UserResponse.builder()
                .username(user.getUsername())
                .name(user.getName())
                .build();
    }

    @Transactional
    public UserResponse update(User user, UpdateUserRequest request){
        validationService.validate(request);
        if(Objects.nonNull(request.getName())){
            user.setName(request.getName());
        }
        if(Objects.nonNull(request.getPassword())){
            user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        }

        userRepository.save(user);
        return UserResponse.builder()
            .name(user.getName())
            .username(user.getUsername())
            .build();
    }
}
