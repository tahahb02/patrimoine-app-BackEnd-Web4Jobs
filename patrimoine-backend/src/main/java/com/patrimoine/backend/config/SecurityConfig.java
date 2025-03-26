    package com.patrimoine.backend.config;

    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.security.web.SecurityFilterChain;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;

    @Configuration
    public class SecurityConfig {

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(csrf -> csrf.disable()) // Désactiver CSRF
                    .authorizeHttpRequests(authz -> authz
                            .anyRequest().permitAll() // Autoriser tout
                    )
                    .formLogin(login -> login.disable()) // Désactiver le formulaire de login
                    .httpBasic(basic -> basic.disable()); // Désactiver l'authentification basique

            return http.build();
        }

        @Bean
        public BCryptPasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }