// src/main/java/com/example/EmployeeDB/configuration/SecurityConfig.java

package com.example.EmployeeDB.configuration;

import com.example.EmployeeDB.filter.JwtAuthenticationFilter;
import com.example.EmployeeDB.filter.JwtAuthorizationFilter;
import com.example.EmployeeDB.service.CustomUserDetailsService;
import com.example.EmployeeDB.util.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * SecurityConfig defines the security configurations for the application,
 * including endpoint access rules, JWT filters, and password encoding.
 */
@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    /**
     * Configures the SecurityFilterChain with specific access rules and JWT filters.
     *
     * @param http the HttpSecurity to modify
     * @return the configured SecurityFilterChain
     * @throws Exception in case of any configuration errors
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Initialize JWT Authentication Filter
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(
                authenticationManager(http.getSharedObject(AuthenticationConfiguration.class)),
                jwtUtil);
        jwtAuthenticationFilter.setFilterProcessesUrl("/api/auth/login"); // Set custom login URL

        // Initialize JWT Authorization Filter
        JwtAuthorizationFilter jwtAuthorizationFilter = new JwtAuthorizationFilter(jwtUtil, userDetailsService);

        http
                // Disable CSRF as tokens are immune to it
                .csrf(AbstractHttpConfigurer::disable)

                // Disable frame options to allow H2 console access
                .headers(headers -> headers.frameOptions().disable())

                // Enable CORS with default settings
                .cors(withDefaults())

                // Set session management to stateless (JWT-based)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Define authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Permit all to access authentication endpoints and AI endpoints
                        .requestMatchers("/api/auth/**", "/api/ai/**").permitAll()

                        // AI Endpoints: Accessible by anyone, including unauthenticated users
                        .requestMatchers("/api/ai/**", "/h2-console/**").permitAll()

                        // Employees Tab
                        // Allow GET requests for any authenticated user
                        .requestMatchers(HttpMethod.GET, "/api/employees/**").authenticated()
                        // Allow POST, PUT, DELETE requests only for ELEVATED and ADMIN roles
                        .requestMatchers(HttpMethod.POST, "/api/employees/**").hasAnyRole("ELEVATED", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/employees/**").hasAnyRole("ELEVATED", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/employees/**").hasAnyRole("ELEVATED", "ADMIN")

                        // Projects Tab
                        // Allow GET requests for any authenticated user
                        .requestMatchers(HttpMethod.GET, "/api/projects/**").authenticated()
                        // Allow POST, PUT, DELETE requests only for ELEVATED and ADMIN roles
                        .requestMatchers(HttpMethod.POST, "/api/projects/**").hasAnyRole("ELEVATED", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/projects/**").hasAnyRole("ELEVATED", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/projects/**").hasAnyRole("ELEVATED", "ADMIN")

                        // Skills Tab
                        // Allow GET requests for any authenticated user
                        .requestMatchers(HttpMethod.GET, "/api/skills/**").authenticated()
                        // Allow POST, PUT, DELETE requests only for ELEVATED and ADMIN roles
                        .requestMatchers(HttpMethod.POST, "/api/skills/**").hasAnyRole("ELEVATED", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/skills/**").hasAnyRole("ELEVATED", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/skills/**").hasAnyRole("ELEVATED", "ADMIN")

                        // Admin Section: Only ADMIN role can access
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Any other request must be authenticated
                        .anyRequest().authenticated()
                )

                // Add JWT filters
                .addFilter(jwtAuthenticationFilter)
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Defines the AuthenticationManager bean using the provided AuthenticationConfiguration.
     *
     * @param authenticationConfiguration the AuthenticationConfiguration
     * @return the AuthenticationManager
     * @throws Exception in case of any configuration errors
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


    /**
     * Defines the PasswordEncoder bean using BCrypt.
     *
     * @return the PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
