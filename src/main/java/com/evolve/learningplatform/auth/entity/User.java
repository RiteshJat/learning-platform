package com.evolve.learningplatform.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.security.AuthProvider;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true)
    private String email;

    private String password; // null for Google users until they set password

    private boolean enabled = false;

    private boolean firstLogin = true; // admin-created users must set password

    @Enumerated(EnumType.STRING)
    private com.evolve.learningplatform.auth.entity.AuthProvider provider; // LOCAL, GOOGLE

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name="user_id"),
            inverseJoinColumns = @JoinColumn(name="role_id")
    )
    private Set<Role> roles;
}

