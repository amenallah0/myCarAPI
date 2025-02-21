package com.myCar.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Allow all origins, headers, and methods for development purposes.
        // In production, consider using more restrictive settings.
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:3000"); // Update with your React app URL
        config.addAllowedHeader("*");
        config.addAllowedMethod(HttpMethod.GET);
        config.addAllowedMethod(HttpMethod.POST);
        config.addAllowedMethod(HttpMethod.PUT);
        config.addAllowedMethod(HttpMethod.DELETE);
        
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .cors()
            .and()
            .csrf().disable()
            .authorizeRequests()
                .antMatchers("/api/users/expert/signup").permitAll() // Permettre l'accès à l'inscription expert
                .antMatchers("/api/users/signin").permitAll()        // Permettre l'accès à la connexion
                .antMatchers("/api/users/**").permitAll()           // Permettre l'accès aux autres endpoints users
                .antMatchers("/users/**").permitAll() // Permit access to these endpoints without authentication
                .antMatchers("/cars/**").permitAll() // Permit access to these endpoints without authentication
                .antMatchers("/api/files/**").permitAll() // Permit access to these endpoints without authentication
                .antMatchers("/api/experts/**").permitAll() // Ajout de la sécurité pour les routes expert
                .antMatchers("/api/users/admin/signup").permitAll()     // Permettre l'accès à l'inscription admin
                .antMatchers("/api/expert-requests/**").permitAll() // Ajout de la sécurité pour les routes expert
                .antMatchers("/api/admin/**").permitAll() // Ajout de la sécurité pour les routes admin
                .antMatchers("/api/admin/expert-requests/**").permitAll() // Ajout de la sécurité pour les routes admin
                .antMatchers("/api/admin/experts/**").permitAll() // Ajout de la sécurité pour les routes admin
                .antMatchers("/api/admin/cars/**").permitAll() // Ajout de la sécurité pour les routes admin
                .antMatchers("/api/admin/sales/**").permitAll() // Ajout de la sécurité pour les routes admin
                .antMatchers("/api/admin/users/**").permitAll() // Ajout de la sécurité pour les routes admin
                .antMatchers(HttpMethod.POST, "/api/annonces").permitAll() // Autoriser uniquement les admins à créer des annonces
                .antMatchers(HttpMethod.GET, "/api/annonces").permitAll() // Autoriser uniquement les admins à créer des annonces
                .antMatchers(HttpMethod.DELETE, "/api/annonces/{id}").permitAll() // Autoriser uniquement les admins à créer des annonces
                .antMatchers(HttpMethod.PUT, "/api/annonces/{id}").permitAll() // Autoriser uniquement les admins à créer des annonces
                .anyRequest().authenticated()
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // Remplacez par l'URL de votre frontend
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
