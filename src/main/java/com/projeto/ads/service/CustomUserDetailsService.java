package com.projeto.ads.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if ("user".equals(username)) {
            // A senha 'password' é codificada com BCrypt.
            // Use um gerador de senhas BCrypt para obter a string codificada
            // para a sua senha real.
            return new User("user", "$2a$10$wE4.EwJj.pG4cWjW4dG.x.sB.0dG.W.y.B0dG.W.y",
                    new ArrayList<>());
        } else {
            throw new UsernameNotFoundException("Usuário não encontrado: " + username);
        }
    }
}
