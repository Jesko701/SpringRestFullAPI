package com.pzn.belajar_spring_boot_pzn.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pzn.belajar_spring_boot_pzn.Entity.User;
import com.pzn.belajar_spring_boot_pzn.Service.ContactService;
import com.pzn.belajar_spring_boot_pzn.model.ContactResponse;
import com.pzn.belajar_spring_boot_pzn.model.CreateContactRequest;
import com.pzn.belajar_spring_boot_pzn.model.WebResponse;

import org.springframework.web.bind.annotation.PostMapping;



@RestController
@RequestMapping("api/contact")
public class ContactController {

    @Autowired
    private ContactService contactService;


    @PostMapping(path="", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<ContactResponse> create(User user, @RequestBody CreateContactRequest request){
        ContactResponse contactResponse = contactService.create(user, request);
        return WebResponse.<ContactResponse>builder().data(contactResponse).build();
    }
}
