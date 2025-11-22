package com.evolve.learningplatform.auth.controller;

import com.evolve.learningplatform.auth.dto.*;
import com.evolve.learningplatform.auth.entity.*;
import com.evolve.learningplatform.auth.repository.*;
import com.evolve.learningplatform.auth.security.JwtUtils;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
//import jakarta.validation.Valid;


import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    // You will also need a ConfirmationTokenService to store & validate tokens and an EmailService

    public AuthController(AuthenticationManager authManager,
                          UserRepository userRepository,
                          RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder,
                          JwtUtils jwtUtils) {
        this.authManager = authManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        // If authentication ok, generate token
        String token = jwtUtils.generateToken(loginRequest.getEmail());
        return ResponseEntity.ok(new LoginResponse(token));
    }

    @PostMapping("/admin/create-user")
    public ResponseEntity<?> adminCreateUser(@RequestBody CreateUserRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists");
        }
        Role role = roleRepository.findByName(req.getRole())
                .orElseGet(() -> roleRepository.save(Role.builder().name(req.getRole()).build()));

        User user = User.builder()
                .email(req.getEmail())
                .enabled(false)
                .firstLogin(true)
                .provider(AuthProvider.LOCAL)
                .roles(Set.of(role))
                .build();

        userRepository.save(user);

        // generate confirmation token (simple evolve)
        String token = UUID.randomUUID().toString();
        // TODO: store token in ConfirmationToken table with expiry and send email with link containing token

        return ResponseEntity.ok("User created. Confirmation email sent (implement email sending).");
    }

    // Endpoint to update password either using token (first-time) or when authenticated
    @PostMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody UpdatePasswordRequest req) {
        // if token provided -> lookup token to find user (ConfirmationTokenService)
        // else if authenticated -> get user from SecurityContextHolder
        // We'll outline the token flow:
        // TODO: validate token, find user, set password, set enabled=true, firstLogin=false
        return ResponseEntity.ok("Implement password update with token validation.");
    }

    @GetMapping("/auth/success")
    public String success() {
        return "Google login success";
    }

}
