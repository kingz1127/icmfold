

package com.example.InnerCityBackend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints (no authentication required)
                        .requestMatchers(
                                "/auth/**",
                                "/error",
                                "/api-docs",
                                "/api-docs/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/webjars/**",
                                "/swagger-resources/**"
                        ).permitAll()

                        // Handle preflight requests
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Public GET endpoints
                        .requestMatchers(HttpMethod.GET, "/categories/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/subcategories/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/outreaches/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/news/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/partners/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/users/me").authenticated()

                        // Authenticated endpoints (any logged-in user)
                        // FIX: bulk-upload specific rule must come BEFORE the general POST /outreaches rule
                        .requestMatchers(HttpMethod.POST, "/outreaches/bulk-upload").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/outreaches").authenticated()

                        // Users - authenticated
                        .requestMatchers("/users/**").authenticated()

                        // Admin-only: Categories
                        .requestMatchers(HttpMethod.POST, "/categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/categories/**").hasRole("ADMIN")

                        // Admin-only: Subcategories
                        .requestMatchers(HttpMethod.POST, "/subcategories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/subcategories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/subcategories/**").hasRole("ADMIN")

                        // Admin-only: Outreaches
                        .requestMatchers(HttpMethod.PUT, "/outreaches/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/outreaches/*/approve").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/outreaches/**").hasRole("ADMIN")

                        // Admin-only: News
                        // FIX: added PATCH to cover the @PatchMapping("/{id}") update endpoint
                        .requestMatchers(HttpMethod.POST, "/news/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/news/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/news/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/news/**").hasRole("ADMIN")

                        // Admin-only: Partners
                        // FIX: replaced the blanket /partners/** rule with method-specific rules
                        // so that GET /partners and GET /partners/{id} remain public (handled above)
                        .requestMatchers(HttpMethod.POST, "/partners/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/partners/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/partners/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",
                "http://localhost:3000",
                "https://icm-web-beta.vercel.app"
        ));
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization", "Content-Type", "Accept",
                "X-Requested-With", "Origin", "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        ));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}