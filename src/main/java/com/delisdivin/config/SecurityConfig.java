package com.delisdivin.config;

import com.delisdivin.security.JwtAuthenticationFilter;
import com.delisdivin.tenant.TenantFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final TenantFilter tenantFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public files
                .requestMatchers("/", "/login", "/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                // Public views and APIs for customers
                .requestMatchers("/restaurant/**", "/api/public/**", "/ws/**").permitAll()
                // Auth APIs
                .requestMatchers("/api/auth/**").permitAll()
                // Super Admin / Register
                .requestMatchers("/super-admin/**", "/api/super-admin/**", "/register").hasRole("SUPER_ADMIN")
                // Restaurant Admin
                .requestMatchers("/restaurant-admin/**").hasRole("RESTAURANT_ADMIN")
                // Kitchen (Chef/Admin)
                .requestMatchers("/kitchen/**").hasAnyRole("RESTAURANT_ADMIN", "CHEF")
                // Server (Server/Admin)
                .requestMatchers("/server/**").hasAnyRole("RESTAURANT_ADMIN", "SERVER")
                // Cashier (Cashier/Admin)
                .requestMatchers("/cashier/**").hasAnyRole("RESTAURANT_ADMIN", "CASHIER")
                // Delivery
                .requestMatchers("/delivery/**").hasRole("DELIVERY")
                // Client Order tracking/profile
                .requestMatchers("/client/**").hasRole("CLIENT")
                // Actuator monitoring
                .requestMatchers("/actuator/**").permitAll()
                .anyRequest().authenticated()
            )
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, authException) -> {
                    // API request: return 401
                    if (request.getRequestURI().startsWith("/api/")) {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                    } else {
                        // Page request: redirect to login
                        response.sendRedirect("/login");
                    }
                })
            )
            // Register filters
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterAfter(tenantFilter, JwtAuthenticationFilter.class);

        return http.build();
    }
}
