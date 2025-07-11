package com.pzn.belajar_spring_boot_pzn.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
}
