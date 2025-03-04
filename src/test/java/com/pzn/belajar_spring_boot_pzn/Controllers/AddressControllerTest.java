package com.pzn.belajar_spring_boot_pzn.Controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pzn.belajar_spring_boot_pzn.Entity.Address;
import com.pzn.belajar_spring_boot_pzn.Entity.Contact;
import com.pzn.belajar_spring_boot_pzn.Entity.User;
import com.pzn.belajar_spring_boot_pzn.Repositories.AddressRepository;
import com.pzn.belajar_spring_boot_pzn.Repositories.ContactRepository;
import com.pzn.belajar_spring_boot_pzn.Repositories.UserRepository;
import com.pzn.belajar_spring_boot_pzn.Security.BCrypt;
import com.pzn.belajar_spring_boot_pzn.model.AddressResponse;
import com.pzn.belajar_spring_boot_pzn.model.CreateAddressRequest;
import com.pzn.belajar_spring_boot_pzn.model.UpdateAddressRequest;
import com.pzn.belajar_spring_boot_pzn.model.WebResponse;

import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.type.TypeReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class AddressControllerTest {

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private ContactRepository contactRepository;

        @Autowired
        private AddressRepository addressRepository;

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @BeforeEach
        void setUp() {
                addressRepository.deleteAll();
                contactRepository.deleteAll();
                userRepository.deleteAll();

                User user = new User();
                user.setUsername("contactTest");
                user.setName("contactTest");
                user.setPassword(BCrypt.hashpw("address", BCrypt.gensalt()));
                user.setToken("addressTest");
                user.setTokenExpiredAt(System.currentTimeMillis() + 1000000);
                userRepository.save(user);

                Contact contact = new Contact();
                contact.setId("test");
                contact.setUser(user);
                contact.setFirstName("joy");
                contact.setLastName("pt");
                contact.setEmail("joy@example.com");
                contact.setPhone("12893198371793");
                contactRepository.save(contact);
        }

        @Test
        void testCreateAddressBadRequest() throws Exception {
                CreateAddressRequest request = new CreateAddressRequest();
                request.setCountry("");
                mockMvc.perform(
                                post("/api/contacts/test/addresses")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .header("X-API-TOKEN", "addressTest")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request)))
                                .andExpectAll(
                                                status().isBadRequest())
                                .andDo(
                                                result -> {
                                                        WebResponse<String> response = objectMapper
                                                                        .readValue(result.getResponse()
                                                                                        .getContentAsString(),
                                                                                        new TypeReference<>() {
                                                                                        });
                                                        assertNotNull(response.getErrors());
                                                });
        }

        @Test
        void testCreateAddressSuccess() throws Exception {
                CreateAddressRequest request = new CreateAddressRequest();
                request.setCountry("USA");
                request.setCity("LA");
                request.setPostalCode("891237614");
                request.setStreet("Jalan Random");
                request.setProvince("America");
                request.setContactId("test");

                mockMvc.perform(
                                post("/api/contacts/test/addresses")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .header("X-API-TOKEN", "addressTest")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request)))
                                .andExpectAll(
                                                status().isCreated())
                                .andDo(
                                                result -> {
                                                        WebResponse<AddressResponse> response = objectMapper
                                                                        .readValue(result.getResponse()
                                                                                        .getContentAsString(),
                                                                                        new TypeReference<>() {
                                                                                        });
                                                        assertNotNull(response.getData());
                                                        assertEquals(response.getData().getCountry(),
                                                                        request.getCountry());
                                                        assertEquals(response.getData().getCity(), request.getCity());
                                                        assertEquals(response.getData().getPostalCode(),
                                                                        request.getPostalCode());
                                                        assertEquals(response.getData().getProvince(),
                                                                        request.getProvince());
                                                        assertEquals(response.getData().getStreet(),
                                                                        request.getStreet());
                                                        assertNull(response.getErrors());
                                                });
        }

        @Test
        void testGetAddressNotFound() throws Exception {
                mockMvc.perform(
                                get("/api/contacts/test/addresses/test")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .header("X-API-TOKEN", "addressTest")
                                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpectAll(
                                                status().isNotFound())
                                .andDo(
                                                result -> {
                                                        WebResponse<String> response = objectMapper
                                                                        .readValue(result.getResponse()
                                                                                        .getContentAsString(),
                                                                                        new TypeReference<>() {
                                                                                        });
                                                        assertNotNull(response.getErrors());
                                                });
        }

        @Test
        void testGetAddressSuccess() throws Exception {
                Contact contact = contactRepository.findById("test").orElseThrow();

                Address address = new Address();
                address.setContactId(contact);
                address.setCity("Jakarta");
                address.setCountry("Indonesia");
                address.setId(UUID.randomUUID().toString());
                address.setPostalCode("1028379710");
                address.setProvince("DKIJakarta");
                address.setStreet("Road Palindrom");
                addressRepository.save(address);

                log.info(address.getId());

                mockMvc.perform(
                                get("/api/contacts/test/addresses/" + address.getId())
                                                .accept(MediaType.APPLICATION_JSON)
                                                .header("X-API-TOKEN", "addressTest")
                                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpectAll(
                                                status().isOk())
                                .andDo(
                                                result -> {
                                                        WebResponse<AddressResponse> response = objectMapper
                                                                        .readValue(result.getResponse()
                                                                                        .getContentAsString(),
                                                                                        new TypeReference<>() {
                                                                                        });
                                                        assertNull(response.getErrors());
                                                        assertEquals(address.getCity(), response.getData().getCity());
                                                        assertEquals(address.getCountry(),
                                                                        response.getData().getCountry());
                                                        assertEquals(address.getPostalCode(),
                                                                        response.getData().getPostalCode());
                                                        assertEquals(address.getId(), response.getData().getId());
                                                        assertEquals(address.getProvince(),
                                                                        response.getData().getProvince());
                                                        assertEquals(address.getStreet(),
                                                                        response.getData().getStreet());
                                                });
        }

        @Test
        void testUpdateAddressBadRequest() throws Exception {
                CreateAddressRequest request = new CreateAddressRequest();
                request.setCountry("");

                mockMvc.perform(
                                put("/api/contacts/test/addresses/test")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .header("X-API-TOKEN", "addressTest")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request)))
                                .andExpectAll(
                                                status().isBadRequest())
                                .andDo(
                                                result -> {
                                                        WebResponse<String> response = objectMapper
                                                                        .readValue(result.getResponse()
                                                                                        .getContentAsString(),
                                                                                        new TypeReference<>() {
                                                                                        });
                                                        assertNotNull(response.getErrors());
                                                });
        }

        @Test
        void testUpdateAddressSuccess() throws Exception {
                Contact contact = contactRepository.findById("test").orElseThrow();

                Address address = new Address();
                address.setContactId(contact);
                address.setCity("Lama");
                address.setCountry("Lama");
                address.setId(UUID.randomUUID().toString());
                address.setPostalCode("Lama");
                address.setProvince("Lama");
                address.setStreet("Lama");
                addressRepository.save(address);

                UpdateAddressRequest request = new UpdateAddressRequest();
                request.setCountry("USA");
                request.setCity("LA");
                request.setPostalCode("891237614");
                request.setStreet("Jalan Random");
                request.setProvince("America");

                mockMvc.perform(
                                put("/api/contacts/test/addresses/" + address.getId())
                                                .accept(MediaType.APPLICATION_JSON)
                                                .header("X-API-TOKEN", "addressTest")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request)))
                                .andExpectAll(
                                                status().isOk())
                                .andDo(
                                                result -> {
                                                        WebResponse<AddressResponse> response = objectMapper
                                                                        .readValue(result.getResponse()
                                                                                        .getContentAsString(),
                                                                                        new TypeReference<>() {
                                                                                        });
                                                        assertNotNull(response.getData());
                                                        assertNull(response.getErrors());
                                                        assertEquals(request.getStreet(),
                                                                        response.getData().getStreet());
                                                        assertEquals(request.getCountry(),
                                                                        response.getData().getCountry());
                                                        assertEquals(request.getPostalCode(),
                                                                        response.getData().getPostalCode());
                                                        assertEquals(request.getStreet(),
                                                                        response.getData().getStreet());
                                                        assertEquals(request.getCity(), response.getData().getCity());
                                                });
        }
}
