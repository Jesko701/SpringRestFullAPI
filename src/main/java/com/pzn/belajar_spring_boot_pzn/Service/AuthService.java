package com.pzn.belajar_spring_boot_pzn.Service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.pzn.belajar_spring_boot_pzn.Entity.User;
import com.pzn.belajar_spring_boot_pzn.Repositories.UserRepository;
import com.pzn.belajar_spring_boot_pzn.Security.BCrypt;
import com.pzn.belajar_spring_boot_pzn.model.LoginUserRequest;
import com.pzn.belajar_spring_boot_pzn.model.TokenResponse;

import jakarta.transaction.Transactional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidationService validationService;

    @Transactional
    public TokenResponse login(LoginUserRequest request) {
        validationService.validate(request);

        User user = userRepository.findById(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or Password wrong"));
        
            if(BCrypt.checkpw(request.getPassword(), user.getPassword())){
                // sukses
                user.setToken(UUID.randomUUID().toString());
                user.setTokenExpiredAt(next30Days());
                userRepository.save(user);
                return TokenResponse.builder()
                    .token(user.getToken())
                    .expiredAt(user.getTokenExpiredAt())
                    .build();
            } else {
                // gagal 
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or Password wrong");
            }
    }

    private Long next30Days(){
        return System.currentTimeMillis() + (1000 * 60 * 24 * 30);
    }

    @Transactional
    public void logout(User user){
        user.setToken(null);
        user.setTokenExpiredAt(null);
        userRepository.save(user);
    }
}
