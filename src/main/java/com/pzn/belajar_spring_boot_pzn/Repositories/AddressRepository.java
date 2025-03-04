package com.pzn.belajar_spring_boot_pzn.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pzn.belajar_spring_boot_pzn.Entity.Address;
import com.pzn.belajar_spring_boot_pzn.Entity.Contact;

@Repository
public interface AddressRepository extends JpaRepository<Address,String>  {
    Optional<Address> findFirstByContactIdAndId(Contact contact, String id);
}
