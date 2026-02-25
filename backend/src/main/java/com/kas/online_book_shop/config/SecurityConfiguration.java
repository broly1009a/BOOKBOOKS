package com.kas.online_book_shop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.kas.online_book_shop.filter.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
        private final JwtAuthenticationFilter jwtAuthenticationFilter;
        private final AuthenticationProvider authenticationProvider;

        @Bean
        @CrossOrigin
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(auth -> auth
                                                // ==================== PUBLIC ENDPOINTS (GUEST) ====================
                                                // Authentication endpoints
                                                .requestMatchers("/api/v1/auth/**").permitAll()
                                                
                                                // Public read-only endpoints
                                                .requestMatchers(HttpMethod.GET, "/api/v1/book/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/v1/feedback/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/v1/language/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/v1/publisher/**").permitAll()
                                                
                                                // ==================== USER/CUSTOMER ENDPOINTS ====================
                                                // Cart management
                                                .requestMatchers("/api/v1/cart/**").hasRole("USER")
                                                
                                                // Customer orders (only own orders)
                                                .requestMatchers(HttpMethod.GET, "/api/v1/order/user/**").hasRole("USER")
                                                .requestMatchers(HttpMethod.POST, "/api/v1/order/process").hasRole("USER")
                                                
                                                // Feedback management (create, update, delete own)
                                                .requestMatchers(HttpMethod.POST, "/api/v1/feedback").hasRole("USER")
                                                .requestMatchers(HttpMethod.PUT, "/api/v1/feedback").hasRole("USER")
                                                
                                                // User profile
                                                .requestMatchers(HttpMethod.GET, "/api/v1/user/{id}").hasAnyRole("USER", "SALE", "MANAGER", "ADMIN")
                                                .requestMatchers(HttpMethod.PUT, "/api/v1/user").hasAnyRole("USER", "MANAGER", "ADMIN")
                                                
                                                // ==================== MANAGER ENDPOINTS ====================
                                                // Book CRUD (full access)
                                                .requestMatchers(HttpMethod.POST, "/api/v1/book").hasRole("MANAGER")
                                                .requestMatchers(HttpMethod.PUT, "/api/v1/book").hasRole("MANAGER")
                                                .requestMatchers(HttpMethod.DELETE, "/api/v1/book/**").hasRole("MANAGER")
                                                .requestMatchers(HttpMethod.GET, "/api/v1/book/change-state/**").hasRole("MANAGER")
                                                
                                                // Feedback moderation
                                                .requestMatchers(HttpMethod.DELETE, "/api/v1/feedback/**").hasAnyRole("MANAGER", "ADMIN")
                                                .requestMatchers(HttpMethod.POST, "/api/v1/feedback/answer").hasAnyRole("MANAGER", "ADMIN")
                                                
                                                // ==================== SALE/STAFF ENDPOINTS ====================
                                                // Order management (full access)
                                                .requestMatchers(HttpMethod.GET, "/api/v1/order/**").hasAnyRole("SALE", "ADMIN")
                                                .requestMatchers(HttpMethod.PUT, "/api/v1/order/update-shipping/**").hasRole("SALE")
                                                .requestMatchers(HttpMethod.PUT, "/api/v1/order/update-orderState/**").hasRole("SALE")
                                                .requestMatchers(HttpMethod.PUT, "/api/v1/order/cancel/**").hasAnyRole("SALE", "USER")
                                                
                                                // Customer management (read-only)
                                                .requestMatchers(HttpMethod.GET, "/api/v1/user/customer").hasAnyRole("SALE", "ADMIN")
                                                .requestMatchers(HttpMethod.GET, "/api/v1/user/by-email/**").hasAnyRole("SALE", "MANAGER", "ADMIN")
                                                
                                                // ==================== ADMIN ENDPOINTS ====================
                                                // Language CRUD
                                                .requestMatchers(HttpMethod.POST, "/api/v1/language").hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.PUT, "/api/v1/language").hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.DELETE, "/api/v1/language/**").hasRole("ADMIN")
                                                
                                                // Publisher CRUD
                                                .requestMatchers(HttpMethod.POST, "/api/v1/publisher").hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.PUT, "/api/v1/publisher").hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.DELETE, "/api/v1/publisher/**").hasRole("ADMIN")
                                                
                                                // User management (full access)
                                                .requestMatchers(HttpMethod.GET, "/api/v1/user/staff").hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.POST, "/api/v1/user/staff/register").hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.PUT, "/api/v1/user/set-account-state/**").hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.PUT, "/api/v1/user/set-role/**").hasRole("ADMIN")
                                                
                                                // Any other request requires authentication
                                                .anyRequest().authenticated()
                                )
                                .sessionManagement(management -> management
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authenticationProvider(authenticationProvider)
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
                return http.build();
        }

}
