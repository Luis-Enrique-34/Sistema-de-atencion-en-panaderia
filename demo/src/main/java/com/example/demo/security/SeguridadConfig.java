package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SeguridadConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filtroDeSeguridad(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(acceso -> acceso
                        .requestMatchers("/login", "/registro", "/css/**", "/error").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/productos/**", "/pedidos/**").hasAnyRole("ADMIN", "PANADERO")
                        .requestMatchers("/tienda/**").hasRole("CLIENTE")
                        .anyRequest().authenticated())
                .formLogin(login -> login
                        .loginPage("/login")
                        .usernameParameter("correo")
                        .defaultSuccessUrl("/menu", true)
                        .permitAll())
                .logout(salida -> salida
                        .logoutSuccessUrl("/login?logout")
                        .permitAll())
                .exceptionHandling(error -> error
                        .accessDeniedPage("/acceso-denegado"));

        return http.build();
    }
}
