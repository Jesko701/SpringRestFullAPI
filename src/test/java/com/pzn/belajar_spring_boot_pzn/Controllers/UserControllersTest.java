package com.pzn.belajar_spring_boot_pzn.Controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.pzn.belajar_spring_boot_pzn.Entity.User;
import com.pzn.belajar_spring_boot_pzn.Repositories.UserRepository;
import com.pzn.belajar_spring_boot_pzn.Security.BCrypt;
import com.pzn.belajar_spring_boot_pzn.model.RegisterUserRequest;
import com.pzn.belajar_spring_boot_pzn.model.UpdateUserRequest;
import com.pzn.belajar_spring_boot_pzn.model.UserResponse;
import com.pzn.belajar_spring_boot_pzn.model.WebResponse;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllersTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private ObjectMapper objectMapper;

        @BeforeEach
        void setUp() {
                userRepository.deleteAll();
        }

        @Test
        void testRegisterSuccess() throws Exception {
                RegisterUserRequest request = new RegisterUserRequest();
                request.setUsername("test");
                request.setPassword("rahasia");
                request.setName("Test");

                // Perform the request and handle async response
                MvcResult mvcResult = mockMvc.perform(post("/api/users")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpectAll(request().asyncStarted()) // Ensure async processing starts
                                .andReturn();

                // Dispatch the async result and assert the response
                mockMvc.perform(asyncDispatch(mvcResult))
                                .andExpectAll(status().isOk())
                                .andDo(result -> {
                                        WebResponse<String> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {
                                                        });
                                        assertEquals("OK", response.getData());
                                });
        }

        @Test
        void testRegisterBadRequest() throws Exception {
                RegisterUserRequest request = new RegisterUserRequest();
                request.setUsername("");
                request.setPassword("");
                request.setName("");

                MvcResult mvcResult = mockMvc.perform(post("/api/users")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpectAll(request().asyncStarted()) // Ensure async processing starts
                                .andReturn();

                // Dispatch the async result and assert the response
                mockMvc.perform(asyncDispatch(mvcResult))
                                .andExpectAll(status().isBadRequest())
                                .andDo(result -> {
                                        WebResponse<String> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {
                                                        });
                                        assertNotNull(response.getErrors());
                                });
        }

        @Test
        void testRegisterDuplicate() throws Exception {
                User user = new User();
                user.setUsername("test");
                user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
                user.setName("Test");
                userRepository.save(user);

                RegisterUserRequest request = new RegisterUserRequest();
                request.setUsername("test");
                request.setPassword("rahasia");
                request.setName("Test");

                MvcResult mvcResult = mockMvc.perform(post("/api/users")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpectAll(request().asyncStarted()) // Ensure async processing starts
                                .andReturn();

                // Dispatch the async result and assert the response
                mockMvc.perform(asyncDispatch(mvcResult))
                                .andExpectAll(status().isBadRequest())
                                .andDo(result -> {
                                        WebResponse<String> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {
                                                        });
                                        assertNotNull(response.getErrors());
                                });
        }

        @Test
        void getUserUnauthorized() throws Exception {
                mockMvc.perform(
                                get("/api/users/current")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .header("X-API-TOKEN", "notfound"))
                                .andExpectAll(status().isUnauthorized()).andDo(result -> {
                                        WebResponse<String> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {
                                                        });
                                        assertNotNull(response.getErrors());
                                });
        }

        @Test
        void getUserUnauthorizedTokenNotSent() throws Exception {
                mockMvc.perform(
                                get("/api/users/current")
                                                .accept(MediaType.APPLICATION_JSON))
                                .andExpectAll(status().isUnauthorized()).andDo(result -> {
                                        WebResponse<String> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {
                                                        });
                                        assertNotNull(response.getErrors());
                                });
        }

        @Test
        void getUserSuccess() throws Exception {
                User user = new User();
                user.setUsername("test");
                user.setName("Test");
                user.setToken("test");
                user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
                user.setTokenExpiredAt(System.currentTimeMillis() + 1000000000000L);
                userRepository.save(user);

                mockMvc.perform(
                                get("/api/users/current")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .header("X-API-TOKEN", "test"))
                                .andExpectAll(status().isOk())
                                .andDo(result -> {
                                        WebResponse<UserResponse> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {
                                                        });
                                        assertNull(response.getErrors());
                                        assertEquals("test", response.getData().getUsername());
                                        assertEquals("Test", response.getData().getName());
                                });
        }

        @Test
        void getUserTokenExpired() throws Exception {
                User user = new User();
                user.setUsername("test");
                user.setName("Test");
                user.setToken("test");
                user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
                user.setTokenExpiredAt(System.currentTimeMillis() - 1000000000000L);
                userRepository.save(user);

                mockMvc.perform(
                                get("/api/users/current")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .header("X-API-TOKEN", "test"))
                                .andExpectAll(status().isUnauthorized())
                                .andDo(result -> {
                                        WebResponse<String> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {
                                                        });
                                        assertNotNull(response.getErrors());
                                });
        }

        @Test
        void updateUserUnauthorized() throws Exception {
                UpdateUserRequest request = new UpdateUserRequest();

                mockMvc.perform(
                                patch("/api/users/current")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request)))
                                .andExpectAll(status().isUnauthorized()).andDo(result -> {
                                        WebResponse<String> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {
                                                        });
                                        assertNotNull(response.getErrors());
                                });
        }

        @Test
        void updateUserSuccess() throws Exception {
                User user = new User();
                user.setUsername("test");
                user.setName("Test");
                user.setToken("test");
                user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
                user.setTokenExpiredAt(System.currentTimeMillis() + 1000000000000L);
                userRepository.save(user);

                UpdateUserRequest request = new UpdateUserRequest();
                String updatedPassword = "eko12345";
                request.setName("Eko");
                request.setPassword(updatedPassword);

                mockMvc.perform(
                                patch("/api/users/current")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request))
                                                .header("X-API-TOKEN", "test"))
                                .andExpectAll(status().isOk()).andDo(result -> {
                                        WebResponse<UserResponse> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {
                                                        });
                                        assertNull(response.getErrors());
                                        assertEquals("Eko", response.getData().getName());
                                        assertEquals("test", response.getData().getUsername());

                                        User userDb = userRepository.findById("test").orElse(null);
                                        assertNotNull(userDb);
                                        assertTrue(BCrypt.checkpw(updatedPassword, userDb.getPassword()));
                                });
        }
}
