package com.pzn.belajar_spring_boot_pzn.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RestController;

import com.pzn.belajar_spring_boot_pzn.Entity.User;
import com.pzn.belajar_spring_boot_pzn.Service.AuthService;
import com.pzn.belajar_spring_boot_pzn.model.LoginUserRequest;
import com.pzn.belajar_spring_boot_pzn.model.TokenResponse;
import com.pzn.belajar_spring_boot_pzn.model.WebResponse;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping(path = "login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<TokenResponse> login(@RequestBody LoginUserRequest request) {
        TokenResponse tokenResponse = authService.login(request);
        return WebResponse.<TokenResponse>builder().data(tokenResponse).build();
    }

    @DeleteMapping(path = "logout")
    public WebResponse<String> logout(User user) {
        authService.logout(user);
        return WebResponse.<String>builder().data("OK").build();
    }

}
