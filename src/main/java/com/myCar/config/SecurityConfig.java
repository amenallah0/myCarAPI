package com.myCar.config;

import java.util.Arrays;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.myCar.domain.User;
import com.myCar.security.JwtAuthenticationFilter;
import com.myCar.security.JwtTokenProvider;
import com.myCar.security.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtTokenProvider tokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Autowired
    public SecurityConfig(JwtTokenProvider tokenProvider, CustomUserDetailsService customUserDetailsService) {
        this.tokenProvider = tokenProvider;
        this.customUserDetailsService = customUserDetailsService;
    }

    public class AuthResponse {
        private String token;
        private User user;
    
        public AuthResponse(String token, User user) {
            this.token = token;
            this.user = user;
        }
    
        // Getters and setters
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public User getUser() { return user; }
        public void setUser(User user) { this.user = user; }
    }

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
        config.addAllowedOrigin("https://mycarsell.vercel.app/"); // Update with your React app URL
        config.addAllowedHeader("*");
        config.addAllowedMethod(HttpMethod.GET);
        config.addAllowedMethod(HttpMethod.POST);
        config.addAllowedMethod(HttpMethod.PUT);
        config.addAllowedMethod(HttpMethod.DELETE);
        
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Override
    public void configure(org.springframework.security.config.annotation.web.builders.WebSecurity web) throws Exception {
        web.ignoring().antMatchers(HttpMethod.POST, "/api/users/refresh-token");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .cors().and().csrf().disable()
            .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/contact").permitAll()
                .antMatchers(HttpMethod.POST, "/api/users/refresh-token").permitAll()
                .antMatchers("/api/users/signin", "/api/users/signup").permitAll()
                .antMatchers("/api/users/expert/signup", "/api/users/admin/signup").permitAll()
                .antMatchers("/api/password/**").permitAll()
                .antMatchers("/api/cars", "/api/cars/**").permitAll()
                .antMatchers("/api/files/**", "/images/**", "/assets/**", "/static/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/annonces", "/api/reviews/car/**").permitAll()
                .antMatchers("/api/cars/latest", "/api/cars/promoted").permitAll()
                .antMatchers("/api/admin/cars/**").permitAll()
                .antMatchers(HttpMethod.PUT, "/api/cars/*/promote").hasAnyRole("USER","EXPERT","ADMIN")
                
                // Routes Admin
                .antMatchers("/api/admin/**").hasRole("ADMIN")
                .antMatchers("/api/admin/expert-requests/**").hasRole("ADMIN")
                .antMatchers("/api/admin/experts/**").hasRole("ADMIN")
                .antMatchers("/api/admin/sales/**").hasRole("ADMIN")
                .antMatchers("/api/admin/users/**").hasRole("ADMIN")
                
                // Routes Expert
                .antMatchers("/api/experts/**").hasRole("EXPERT")
                .antMatchers(HttpMethod.POST, "/api/experts/expertise-requests/*/submit-report").hasRole("EXPERT")
                .antMatchers("/api/expert-requests/**").hasAnyRole("USER", "ADMIN")
                
                // Routes Annonces
                .antMatchers(HttpMethod.POST, "/api/annonces").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.DELETE, "/api/annonces/{id}").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.PUT, "/api/annonces/{id}").hasAnyRole("USER", "ADMIN")
                
                // Routes Expertise
                .antMatchers("/api/expertise-requests/**").authenticated()
                .antMatchers("/api/expertise-requests/expert/**").hasRole("EXPERT")
                
                // Routes Notifications
                .antMatchers("/api/notifications/**").authenticated()
                
                // Routes Paiements
                .antMatchers("/api/payments/generate-link").authenticated()
                .antMatchers("/api/payments/**").authenticated()
                
                // Routes Reviews
                .antMatchers(HttpMethod.POST, "/api/reviews").hasAnyRole("USER", "EXPERT")
                .antMatchers("/api/reviews/**").authenticated()
                
                // Routes Users
                .antMatchers(HttpMethod.GET, "/api/users/**").authenticated()
                .antMatchers(HttpMethod.PUT, "/api/users/**").authenticated()
                .antMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN")
                
                // Routes Expertise Count
                .antMatchers("/api/users/experts/{expertId}/expertise-count").hasAnyRole("EXPERT", "ADMIN")
                .antMatchers("/api/users/expertise-count").authenticated()
                
                // Route par défaut (doit être la dernière)
                .anyRequest().authenticated()
            .and()
            .exceptionHandling()
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setContentType("application/json");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"error\": \"" + authException.getMessage() + "\"}");
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setContentType("application/json");
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"" + accessDeniedException.getMessage() + "\"}");
                })
            .and()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .addFilterBefore(new JwtAuthenticationFilter(tokenProvider, customUserDetailsService),
                    UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new OncePerRequestFilter() {
                @Override
                protected void doFilterInternal(HttpServletRequest request,
                                             HttpServletResponse response,
                                             FilterChain filterChain)
                        throws ServletException, IOException {
                    System.out.println("Request URL: " + request.getRequestURL());
                    System.out.println("Request Method: " + request.getMethod());
                    System.out.println("Authorization header: " + request.getHeader("Authorization"));
                    filterChain.doFilter(request, response);
                }
            }, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedOrigins(Arrays.asList("https://mycarsell.vercel.app/")); // Remplacez par l'URL de votre frontend
        // Remplacez par l'URL de votre frontend
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    // @Bean
    // public ObjectMapper objectMapper() {
    //     ObjectMapper mapper = new ObjectMapper();
    //     mapper.registerModule(new JavaTimeModule());
    //     return mapper;
    // }
}
