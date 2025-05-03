package com.connectly_cm.Connectly_CM.config;

import com.connectly_cm.Connectly_CM.models.users.User;
import com.connectly_cm.Connectly_CM.repositories.userRepository.UserRepository;
import org.checkerframework.checker.units.qual.Acceleration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword()) // or "" if using JWT-only auth
                .authorities((GrantedAuthority) user.getRoles()) // Convert your roles to GrantedAuthority
                .build();
    }
}
