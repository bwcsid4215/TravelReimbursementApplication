package com.rentkaro.userservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Entity
@Table(name = "users")
@Data
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "First name is required")
    @Size(min = 3, max = 50, message = "First name must be between 3 to 50 characters")
    @Column(nullable = false)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 3, max = 50, message = "Last name must be between 3 to 50 characters")
    @Column(nullable = false)
    private String lastName;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 to 50 characters")
    @Column(nullable = false, unique = true)
    private String userName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean enabled = true;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    // Spring Security methods

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles; // ✅ Roles implement GrantedAuthority
    }

    @Override
    public String getUsername() {
        return this.userName; // ✅ Use username for authentication
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // ✅ Allow account by default
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // ✅ Allow account by default
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // ✅ Allow credentials by default
    }

    @Override
    public boolean isEnabled() {
        return this.enabled; // ✅ Use actual enabled status
    }
}
