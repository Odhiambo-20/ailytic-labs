package com.bellatechnologies.backend.security;

import com.bellatechnologies.backend.model.User;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Getter
public class CustomUserDetails implements UserDetails, OAuth2User {

    private final User user;
    private Map<String, Object> attributes;

    // Constructor for regular authentication (username/password)
    public CustomUserDetails(User user) {
        this.user = user;
    }

    // Constructor for OAuth2 authentication
    public CustomUserDetails(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    // OAuth2User method
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    // OAuth2User method
    @Override
    public String getName() {
        return user.getUserId();
    }

    // UserDetails methods
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail(); // Using email as username
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
        return user.isEnabled();
    }

    // Additional helper methods
    public String getUserId() {
        return user.getUserId();
    }

    public String getEmail() {
        return user.getEmail();
    }

    public String getFirstName() {
        return user.getFirstName();
    }

    public String getLastName() {
        return user.getLastName();
    }
}
