package com.pzn.belajar_spring_boot_pzn.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pzn.belajar_spring_boot_pzn.Service.UserService;
import com.pzn.belajar_spring_boot_pzn.model.RegisterUserRequest;
import com.pzn.belajar_spring_boot_pzn.model.WebResponse;

@RestController
@RequestMapping("api/users")
public class UserControllers {
    @Autowired
    private UserService userService;

    @PostMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<String> register(@RequestBody RegisterUserRequest request) {
        userService.register(request);
        return WebResponse.<String>builder().data("OK").build();
    }
}
