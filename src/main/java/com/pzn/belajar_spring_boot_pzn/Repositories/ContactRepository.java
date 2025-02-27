package com.pzn.belajar_spring_boot_pzn.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pzn.belajar_spring_boot_pzn.Entity.Contact;

@Repository
public interface ContactRepository extends JpaRepository<Contact, String> {

    
} 