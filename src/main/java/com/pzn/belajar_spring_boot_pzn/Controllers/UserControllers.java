package com.pzn.belajar_spring_boot_pzn.Controllers;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.pzn.belajar_spring_boot_pzn.Entity.User;
import com.pzn.belajar_spring_boot_pzn.Service.UserService;
import com.pzn.belajar_spring_boot_pzn.model.RegisterUserRequest;
import com.pzn.belajar_spring_boot_pzn.model.UpdateUserRequest;
import com.pzn.belajar_spring_boot_pzn.model.UserResponse;
import com.pzn.belajar_spring_boot_pzn.model.WebResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;

@RestController
@RequestMapping("api/users")
public class UserControllers {
    @Autowired
    private UserService userService;

    @PostMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public CompletableFuture<WebResponse<String>> register(@RequestBody RegisterUserRequest request) {
        return userService.register(request)
                .thenApply(unused -> WebResponse.<String>builder().data("OK").build())
                .exceptionally(ex -> {
                    if (ex.getCause() instanceof ConstraintViolationException) {
                        // Handle validation errors
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getCause().getMessage());
                    } else if (ex.getCause() instanceof ResponseStatusException) {
                        // Handle business logic errors (e.g., duplicate username)
                        throw (ResponseStatusException) ex.getCause();
                    } else {
                        // Handle unexpected errors
                        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                                "An unexpected error occurred");
                    }
                });
    }

    @GetMapping(path = "current", produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<UserResponse> get(User user) {
        UserResponse userResponse = userService.get(user);
        return WebResponse.<UserResponse>builder().data(userResponse).build();
    }

    @PatchMapping(path = "current", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<UserResponse> update(User user, @RequestBody UpdateUserRequest request) {
        UserResponse userResponse = userService.update(user, request);
        return WebResponse.<UserResponse>builder().data(userResponse).build();
    }

}
