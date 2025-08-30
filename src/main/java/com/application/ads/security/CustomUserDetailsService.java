package com.application.ads.security;

import com.application.ads.model.Admin;
import com.application.ads.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Admin> adminOptional = adminRepository.findByEmail(email);
        if (adminOptional.isPresent()) {
            Admin admin = adminOptional.get();
            // Retorna um UserDetails com o e-mail e a senha,
            // e a role como uma SimpleGrantedAuthority
            return new User(admin.getEmail(), admin.getPasswordHash(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + admin.getRole())));
        } else {
            throw new UsernameNotFoundException("Usuário não encontrado com e-mail: " + email);
        }
    }
}