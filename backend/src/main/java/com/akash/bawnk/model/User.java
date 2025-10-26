package com.akash.bawnk.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Builder; // Import Builder
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor; // Import AllArgsConstructor
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "app_user")
@Data
@Builder // Add Builder
@NoArgsConstructor
@AllArgsConstructor // Add AllArgsConstructor
public class User implements UserDetails { // Implement UserDetails

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username; // This will be our unique identifier

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true) // Make email unique
    private String email;

    private String firstName;
    private String lastName;

    @Enumerated(EnumType.STRING) // Define a role
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Account> accounts;

    // --- UserDetails Methods ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
    	
    	if(null==this.role)
    		return List.of();
    	
        return List.of(new SimpleGrantedAuthority("ROLE_"+this.role.name()));
        
    }

    // We already have getPassword() from @Data

    // We will use 'username' for authentication
    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}