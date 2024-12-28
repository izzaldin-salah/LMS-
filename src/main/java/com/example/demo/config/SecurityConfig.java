package com.example.demo.config;

import com.example.demo.services.JwtAuthFilter;
import com.example.demo.services.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public JwtAuthFilter jwtAuthFilter() {
        return new JwtAuthFilter(jwtUtil);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/users/login").permitAll()
                .requestMatchers("/users/register").hasRole("ADMIN")
                .requestMatchers("/performance/**").hasAnyRole("INSTRUCTOR", "ADMIN")
                .requestMatchers("/courses/{courseId}/enroll").hasRole("STUDENT")
                .requestMatchers("/courses/lessons/{lessonId}/attend").hasRole("STUDENT")
                .requestMatchers("/courses/**").hasAnyRole("INSTRUCTOR", "ADMIN")
                .requestMatchers("/assessments/**").hasAnyRole("STUDENT", "INSTRUCTOR", "ADMIN")
                .requestMatchers("/assessments/{assessmentId}/questions").hasAnyRole("INSTRUCTOR","ADMIN")
                .requestMatchers("/assessments/{submissionId}/grade-assignment").hasAnyRole("STUDENT", "INSTRUCTOR", "ADMIN")
                .requestMatchers("/notifications/**").hasAnyRole("STUDENT", "INSTRUCTOR", "ADMIN")

                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
