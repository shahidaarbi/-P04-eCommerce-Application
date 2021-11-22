package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.security.UserDetailsServiceImpl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);
    private UserDetailsService userDetailsService = mock(UserDetailsServiceImpl.class);

    @Before
    public void setUp(){
        userController = new UserController();
        TestUtils.injectObject(userController,"userRepository", userRepository);
        TestUtils.injectObject(userController, "cartRepository", cartRepository);
        TestUtils.injectObject(userController, "bCryptPasswordEncoder", encoder);
        TestUtils.injectObject(userController, "userDetailsService", userDetailsService);
    }

    @Test
    public void create_user_happy_path(){
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("Shahida");
        request.setPassword("password123");
        request.setConfirmPassword("password123");
        when(encoder.encode("password123")).thenReturn("hasedPassword");

        final ResponseEntity<?> response = this.userController.createUser(request);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User user = (User) response.getBody();
        assertNotNull(user);
        assertEquals("Shahida", user.getUsername());
        assertEquals("hasedPassword", user.getPassword());
    }
    @Test
    public void create_user_fail_case_length(){
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("Shahida");
        request.setPassword("123");
        request.setConfirmPassword("123");
        when(encoder.encode("123")).thenReturn("hasedPassword");

        final ResponseEntity<?> response = this.userController.createUser(request);
  //      assertNull(response);
        assertEquals(400, response.getStatusCodeValue());

       // assertEquals(200, response.getStatusCodeValue());
        //User user = (User) response.getBody();
       // assertNull(user);
      //  assertEquals("Shahida", user.getUsername());
        //assertEquals("hasedPassword", user.getPassword());
    }
    @Test
    public void create_user_fail_case_pasword_not_match(){
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("Shahida");
        request.setPassword("12345678");
        request.setConfirmPassword("123456789");
        when(encoder.encode("12345678")).thenReturn("hasedPassword");

        final ResponseEntity<?> response = this.userController.createUser(request);
  //      assertNull(response);
        assertEquals(400, response.getStatusCodeValue());

       // assertEquals(200, response.getStatusCodeValue());
        //User user = (User) response.getBody();
        //assertNull(user);
      //  assertEquals("Shahida", user.getUsername());
        //assertEquals("hasedPassword", user.getPassword());
    }
    @Test
    public void get_user_by_id(){
        User user = new User();
        user.setUsername("Shahida");
        user.setPassword("password123");
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user));
        
        ResponseEntity<User> response = userController.findById(1L);
        
        User CurrentUser = response.getBody();
        Assert.assertNotNull(CurrentUser);
        Assert.assertEquals(1L, CurrentUser.getId());

        Assert.assertEquals("Shahida", CurrentUser.getUsername());
        Assert.assertEquals("password123", CurrentUser.getPassword());
    }

    @Test
    public void get_user_by_username(){
        User user = new User();
        user.setUsername("Shahida");
        user.setPassword("password123");
        user.setId(1L);

        when(userRepository.findByUsername("Shahida")).thenReturn(user);
        ResponseEntity<User> response = userController.findByUserName("Shahida");
        
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());

        User CurrentUser = response.getBody();
        Assert.assertNotNull(CurrentUser);
        Assert.assertEquals(1L, CurrentUser.getId());
        Assert.assertEquals("Shahida", CurrentUser.getUsername());
        Assert.assertEquals("password123", CurrentUser.getPassword());
    }
}
