package com.evolve.learningplatform.auth.security;

import com.evolve.learningplatform.auth.entity.*;
import com.evolve.learningplatform.auth.repository.*;
import jakarta.servlet.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import java.io.IOException;
import java.util.Set;

public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public OAuth2LoginSuccessHandler(JwtUtils jwtUtils,
                                     UserRepository userRepository,
                                     RoleRepository roleRepository) {
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();

        String email = oidcUser.getAttribute("email"); // safest

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_USER").build()));

            User u = User.builder()
                    .email(email)
                    .enabled(true)
                    .firstLogin(false)
                    .provider(AuthProvider.GOOGLE)
                    .roles(Set.of(userRole))
                    .build();

            return userRepository.save(u);
        });

        String token = jwtUtils.generateToken(email);

        response.sendRedirect("http://localhost:4200/auth/success?token=" + token);
    }
}

