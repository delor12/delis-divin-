package com.delisdivin.security;

import com.delisdivin.entity.AppUser;
import com.delisdivin.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@AllArgsConstructor
@Getter
public class UserDetailsImpl implements UserDetails {

    private final Long id;
    private final String username;
    private final String email;
    private final String password;
    private final Role role;
    private final Long restaurantId;
    private final boolean active;

    public static UserDetailsImpl build(AppUser user) {
        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getRole(),
                user.getRestaurant() != null ? user.getRestaurant().getId() : null,
                user.isActive()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

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
        return active;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}
