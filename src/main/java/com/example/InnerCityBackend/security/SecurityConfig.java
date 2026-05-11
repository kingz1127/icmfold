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
import java.util.Collections;

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
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/api-docs", "/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        .requestMatchers(
                                "/auth/**",
                                "/error",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/webjars/**",
                                "/swagger-resources/**"
                        ).permitAll()

                        // ADD /api/v1 prefix to all these paths
                        .requestMatchers(HttpMethod.POST, "/api/v1/categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/categories/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/v1/subcategories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/subcategories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/subcategories/**").hasRole("ADMIN")

                        // OUTREACH
                        .requestMatchers(HttpMethod.GET, "/api/v1/outreaches/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/outreaches").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/outreaches/bulk-upload").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/outreaches/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/outreaches/*/approve").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/outreaches/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/categories/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/subcategories/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/v1/news/**").permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/v1/news/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/news/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/news/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/v1/partners/**").permitAll()
                        .requestMatchers("/api/v1/partners/**").hasRole("ADMIN")

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
                "X-Requested-With", "Origin"
        ));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}