package com.example.demo.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.SareetaApplication;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.security.UserDetailsServiceImpl;

@RestController
@RequestMapping("/api/user")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;
	static final Logger logger = LoggerFactory.getLogger(SareetaApplication.class);
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired private UserDetailsServiceImpl userDetailsService;
	
	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		logger.info("*******Getting user by id: " + id);
		return ResponseEntity.of(userRepository.findById(id));
	}
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		logger.info("*******Getting user by username " + username);
		User user = userRepository.findByUsername(username);
		return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
	}
	
	@PostMapping("/create")
	public ResponseEntity<?> createUser(@RequestBody CreateUserRequest createUserRequest) {
		try{
			User user = new User();
			if(userRepository.findByUsername(createUserRequest.getUsername()) != null) {
				logger.error("*******Error creating duplicate user..." + createUserRequest.getUsername());
				throw new Exception("This user already exists.");
			}
			user.setUsername(createUserRequest.getUsername());
			logger.info("*******user name set with " + createUserRequest.getUsername());
			Cart cart = new Cart();
			cartRepository.save(cart);
			user.setCart(cart);

			if(createUserRequest.getPassword().length() < 7 ||
					!createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())){
				logger.error("*******Error creating user..." + createUserRequest.getUsername());
				throw new Exception(" Passwords must match and length must be 7 or more characters.");
			}
			user.setSalt(userDetailsService.createSalt());
			userDetailsService.get_SecurePassword("SHA-256", createUserRequest.getPassword(), user.getSalt());
			user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));

			userRepository.save(user);
			logger.info("*******Create user success", createUserRequest.getUsername());
			return ResponseEntity.ok(user);
		}
		catch(Exception e){
			logger.error("*****Error on create user " + createUserRequest.getUsername()  +e.getMessage());
			return ResponseEntity.badRequest().body(
					"Error on create user " + createUserRequest.getUsername()
					+ ": " + e.getMessage());
		}
	}
	
}
