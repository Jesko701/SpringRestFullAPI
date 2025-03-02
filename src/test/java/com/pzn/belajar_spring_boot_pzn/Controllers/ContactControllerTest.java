package com.pzn.belajar_spring_boot_pzn.Controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.core.type.TypeReference;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pzn.belajar_spring_boot_pzn.Entity.Contact;
import com.pzn.belajar_spring_boot_pzn.Entity.User;
import com.pzn.belajar_spring_boot_pzn.Repositories.ContactRepository;
import com.pzn.belajar_spring_boot_pzn.Repositories.UserRepository;
import com.pzn.belajar_spring_boot_pzn.Security.BCrypt;
import com.pzn.belajar_spring_boot_pzn.model.ContactResponse;
import com.pzn.belajar_spring_boot_pzn.model.CreateContactRequest;
import com.pzn.belajar_spring_boot_pzn.model.UpdateContactRequest;
import com.pzn.belajar_spring_boot_pzn.model.WebResponse;

import lombok.extern.slf4j.Slf4j;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class ContactControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private ContactRepository contactRepository;

        @Autowired
        private ObjectMapper objectMapper;

        @BeforeEach
        void setUp() {
                contactRepository.deleteAll();
                userRepository.deleteAll();

                User user = new User();
                user.setUsername("contactTest");
                user.setName("contactTest");
                user.setPassword(BCrypt.hashpw("contactTest", BCrypt.gensalt()));
                user.setToken("contactTest");
                user.setTokenExpiredAt(System.currentTimeMillis() + 1000000);
                userRepository.save(user);
        }

        @Test
        void testCreateContactBadRequest() throws Exception {
                CreateContactRequest request = new CreateContactRequest();
                request.setFirstName("");
                request.setEmail("salah");

                mockMvc.perform(
                                post("/api/contacts")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request))
                                                .header("X-API-TOKEN", "contactTest"))
                                .andExpectAll(
                                                status().isBadRequest())
                                .andDo(result -> {
                                        WebResponse<String> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<WebResponse<String>>() {
                                                        });
                                        assertNotNull(response.getErrors());
                                });
        }

        @Test
        void testCreateContactSuccess() throws Exception {
                CreateContactRequest request = new CreateContactRequest();
                request.setEmail("ekoKhannedy@example.com");
                request.setFirstName("eko");
                request.setLastName("khannedy");
                request.setPhone("2141221341212");

                mockMvc.perform(
                                post("/api/contacts")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request))
                                                .header("X-API-TOKEN", "contactTest"))
                                .andExpectAll(
                                                status().isOk())
                                .andDo(result -> {
                                        WebResponse<ContactResponse> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<WebResponse<ContactResponse>>() {
                                                        });
                                        assertNull(response.getErrors());
                                        assertNotNull(response.getData());
                                        assertEquals("ekoKhannedy@example.com", response.getData().getEmail());
                                        assertEquals("eko", response.getData().getFirstName());
                                        assertEquals("khannedy", response.getData().getLastName());
                                        assertEquals("2141221341212", response.getData().getPhone());

                                        assertTrue(contactRepository.existsById(response.getData().getId()));
                                });
        }

        @Test
        void testGetContactNotFound() throws Exception {
                mockMvc.perform(
                                get("/api/contacts/941288213761283")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .header("X-API-TOKEN", "contactTest"))
                                .andExpectAll(
                                                status().isNotFound())
                                .andDo(result -> {
                                        WebResponse<String> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<WebResponse<String>>() {
                                                        });
                                        assertNotNull(response.getErrors());
                                });
        }

        @Test
        void testGetContactSuccess() throws Exception {
                User user = userRepository.findById("contactTest").orElseThrow();

                Contact contact = new Contact();
                contact.setId(UUID.randomUUID().toString());
                contact.setUser(user);
                contact.setFirstName("joy");
                contact.setLastName("pt");
                contact.setEmail("joy@example.com");
                contact.setPhone("12893198371793");
                contactRepository.save(contact);

                mockMvc.perform(
                                get("/api/contacts/" + contact.getId())
                                                .accept(MediaType.APPLICATION_JSON)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .header("X-API-TOKEN", "contactTest"))
                                .andExpectAll(
                                                status().isOk())
                                .andDo(result -> {
                                        WebResponse<ContactResponse> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<WebResponse<ContactResponse>>() {
                                                        });
                                        assertNull(response.getErrors());

                                        assertEquals(contact.getId(), response.getData().getId());
                                        assertEquals(contact.getFirstName(), response.getData().getFirstName());
                                        assertEquals(contact.getLastName(), response.getData().getLastName());
                                        assertEquals(contact.getPhone(), response.getData().getPhone());
                                        assertEquals(contact.getEmail(), response.getData().getEmail());
                                });
        }

        @Test
        void testUpdateContactBadRequest() throws Exception {
                UpdateContactRequest request = new UpdateContactRequest();
                request.setFirstName("");
                request.setEmail("salah");

                mockMvc.perform(
                                put("/api/contacts/1312321312421412")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request))
                                                .header("X-API-TOKEN", "contactTest"))
                                .andExpectAll(
                                                status().isBadRequest())
                                .andDo(result -> {
                                        WebResponse<String> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<WebResponse<String>>() {
                                                        });
                                        assertNotNull(response.getErrors());
                                });
        }

        @Test
        void testUpdateContactSuccess() throws Exception {
                User user = userRepository.findById("contactTest").orElseThrow();

                Contact contact = new Contact();
                contact.setId(UUID.randomUUID().toString());
                contact.setUser(user);
                contact.setFirstName("joy");
                contact.setLastName("pt");
                contact.setEmail("joy@example.com");
                contact.setPhone("12893198371793");
                contactRepository.save(contact);

                CreateContactRequest request = new CreateContactRequest();
                request.setEmail("abcTesting@example.com");
                request.setFirstName("abc");
                request.setLastName("lima dasar");
                request.setPhone("129831u93281");

                mockMvc.perform(
                                put("/api/contacts/" + contact.getId())
                                                .accept(MediaType.APPLICATION_JSON)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .header("X-API-TOKEN", "contactTest")
                                                .content(objectMapper.writeValueAsString(request)))
                                .andExpectAll(
                                                status().isOk())
                                .andDo(result -> {
                                        WebResponse<ContactResponse> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<WebResponse<ContactResponse>>() {
                                                        });

                                        assertNull(response.getErrors());

                                        assertEquals(contact.getId(), response.getData().getId());
                                        assertEquals(request.getFirstName(), response.getData().getFirstName());
                                        assertEquals(request.getLastName(), response.getData().getLastName());
                                        assertEquals(request.getPhone(), response.getData().getPhone());
                                        assertEquals(request.getEmail(), response.getData().getEmail());
                                });
        }

        @Test
        void testDeleteContactNotFound() throws Exception {
                mockMvc.perform(
                                delete("/api/contacts/9012830123890194631871246781")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .header("X-API-TOKEN", "contactTest"))
                                .andExpectAll(
                                                status().isNotFound())
                                .andDo(result -> {
                                        WebResponse<String> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<WebResponse<String>>() {
                                                        });
                                        assertNotNull(response.getErrors());
                                });
        }

        @Test
        void testDeleteContactSuccess() throws Exception {

                User user = userRepository.findById("contactTest").orElseThrow();

                Contact contact = new Contact();
                contact.setId(UUID.randomUUID().toString());
                contact.setUser(user);
                contact.setFirstName("joy");
                contact.setLastName("pt");
                contact.setEmail("joy@example.com");
                contact.setPhone("12893198371793");
                contactRepository.save(contact);

                mockMvc.perform(
                                delete("/api/contacts/" + contact.getId())
                                                .accept(MediaType.APPLICATION_JSON)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .header("X-API-TOKEN", "contactTest"))
                                .andExpectAll(
                                                status().isOk())
                                .andDo(result -> {
                                        WebResponse<String> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<WebResponse<String>>() {
                                                        });
                                        assertNull(response.getErrors());
                                });
        }

        @Test
        void searchNotFound() throws Exception {

                mockMvc.perform(
                                get("/api/contacts")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .header("X-API-TOKEN", "contactTest"))
                                .andExpectAll(
                                                status().isOk())
                                .andDo(result -> {
                                        WebResponse<List<ContactResponse>> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {
                                                        });
                                        assertNull(response.getErrors());
                                        assertEquals(0, response.getData().size());
                                        assertEquals(0, response.getPaging().getTotalPage());
                                        assertEquals(0, response.getPaging().getCurrentPage());
                                        assertEquals(10, response.getPaging().getSize());
                                });
        }

        @Test
        void searchUsingName() throws Exception {
                User user = userRepository.findById("contactTest").orElseThrow();

                for (int i = 0; i < 100; i++) {
                        Contact contact = new Contact();
                        contact.setId(UUID.randomUUID().toString());
                        contact.setUser(user);
                        contact.setFirstName("joy" + i);
                        contact.setLastName("pt");
                        contact.setEmail("joy@example.com");
                        contact.setPhone("12893198371793");
                        contactRepository.save(contact);
                }

                mockMvc.perform(
                                get("/api/contacts")
                                                .queryParam("name", "joy")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .header("X-API-TOKEN", "contactTest"))
                                .andExpectAll(
                                                status().isOk())
                                .andDo(result -> {
                                        WebResponse<List<ContactResponse>> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {
                                                        });
                                        assertNull(response.getErrors());
                                        assertEquals(10, response.getData().size());
                                        assertEquals(10, response.getPaging().getTotalPage());
                                        assertEquals(0, response.getPaging().getCurrentPage());
                                        assertEquals(10, response.getPaging().getSize());
                                });
        }

        @Test
        void searchSuccess() throws Exception {
                User user = userRepository.findById("contactTest").orElseThrow();

                for (int i = 0; i < 100; i++) {
                        Contact contact = new Contact();
                        contact.setId(UUID.randomUUID().toString());
                        contact.setUser(user);
                        contact.setFirstName("joy" + i);
                        contact.setLastName("pt");
                        contact.setEmail("joy@example.com");
                        contact.setPhone("12893198371793");
                        contactRepository.save(contact);
                        log.info(Thread.currentThread().getName());
                }

                mockMvc.perform(
                                get("/api/contacts")
                                                .queryParam("email", "example.com")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .header("X-API-TOKEN", "contactTest"))
                                .andExpectAll(
                                                status().isOk())
                                .andDo(result -> {
                                        WebResponse<List<ContactResponse>> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {
                                                        });
                                        assertNull(response.getErrors());
                                        assertEquals(10, response.getData().size());
                                        assertEquals(10, response.getPaging().getTotalPage());
                                        assertEquals(0, response.getPaging().getCurrentPage());
                                        assertEquals(10, response.getPaging().getSize());
                                });

                mockMvc.perform(
                                get("/api/contacts")
                                                .queryParam("name", "p")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .header("X-API-TOKEN", "contactTest"))
                                .andExpectAll(
                                                status().isOk())
                                .andDo(result -> {
                                        WebResponse<List<ContactResponse>> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {
                                                        });
                                        assertNull(response.getErrors());
                                        assertEquals(10, response.getData().size());
                                        assertEquals(10, response.getPaging().getTotalPage());
                                        assertEquals(0, response.getPaging().getCurrentPage());
                                        assertEquals(10, response.getPaging().getSize());
                                });

                mockMvc.perform(
                                get("/api/contacts")
                                                .queryParam("email", "example.com")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .header("X-API-TOKEN", "contactTest"))
                                .andExpectAll(
                                                status().isOk())
                                .andDo(result -> {
                                        WebResponse<List<ContactResponse>> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {
                                                        });
                                        assertNull(response.getErrors());
                                        assertEquals(10, response.getData().size());
                                        assertEquals(10, response.getPaging().getTotalPage());
                                        assertEquals(0, response.getPaging().getCurrentPage());
                                        assertEquals(10, response.getPaging().getSize());
                                });

                mockMvc.perform(
                                get("/api/contacts")
                                                .queryParam("phone", "71793")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .header("X-API-TOKEN", "contactTest"))
                                .andExpectAll(
                                                status().isOk())
                                .andDo(result -> {
                                        WebResponse<List<ContactResponse>> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {
                                                        });
                                        assertNull(response.getErrors());
                                        assertEquals(10, response.getData().size());
                                        assertEquals(10, response.getPaging().getTotalPage());
                                        assertEquals(0, response.getPaging().getCurrentPage());
                                        assertEquals(10, response.getPaging().getSize());
                                });

                mockMvc.perform(
                                get("/api/contacts")
                                                .queryParam("phone", "71793")
                                                .queryParam("page", "1000")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .header("X-API-TOKEN", "contactTest"))
                                .andExpectAll(
                                                status().isOk())
                                .andDo(result -> {
                                        WebResponse<List<ContactResponse>> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {
                                                        });
                                        assertNull(response.getErrors());
                                        assertEquals(0, response.getData().size());
                                        assertEquals(10, response.getPaging().getTotalPage());
                                        assertEquals(1000, response.getPaging().getCurrentPage());
                                        assertEquals(10, response.getPaging().getSize());
                                });
        }
}
