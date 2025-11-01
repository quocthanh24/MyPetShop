package com.thanhluu.tlcn.Config;

import com.thanhluu.tlcn.Entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserDetailConfig implements UserDetails {

    private String username; // Changed from 'name' to 'email' for clarity
    private String password;
    private GrantedAuthority authority;

    public UserDetailConfig(UserEntity userEntity) {
        this.username = userEntity.getGmail(); // Use gmail as username
        this.password = userEntity.getPassword();
        this.authority = new SimpleGrantedAuthority("ROLE_" + userEntity.getRole().name());
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(authority);
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
//        return UserDetails.super.isAccountNonExpired();
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
//        return UserDetails.super.isAccountNonLocked();
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
//        return UserDetails.super.isCredentialsNonExpired();
        return true;
    }

    @Override
    public boolean isEnabled() {
//        return UserDetails.super.isEnabled();
        return true;
    }
}
