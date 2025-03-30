package dev.filipe.daminhaconta.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)  // Desabilita CSRF (se necessário)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()) // Permite tudo
                .formLogin(AbstractHttpConfigurer::disable) // Desabilita tela de login
                .httpBasic(AbstractHttpConfigurer::disable); // Desabilita autenticação básica

        return http.build();
    }
}

