package com.thinkitdevit.demospringjwt.security;

import com.thinkitdevit.demospringjwt.security.jwt.AuthEntryPointJwt;
import com.thinkitdevit.demospringjwt.security.jwt.AuthTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
public class WebSecurityConf {


    private AuthEntryPointJwt authenticationEntryPoint;

    private UserDetailsService userDetailsService;

    private AuthTokenFilter requestFilter;

    @Autowired
    public WebSecurityConf(AuthEntryPointJwt authenticationEntryPoint,
                           UserDetailsService userDetailsService,
                           AuthTokenFilter requestFilter) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.userDetailsService = userDetailsService;
        this.requestFilter = requestFilter;
    }



    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, @Value("${app.cors.allowed-origins}") String[] allowedOrigins) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(
                        request -> {
                            CorsConfiguration corsConf = new CorsConfiguration();
                            corsConf.setAllowedOrigins(Arrays.stream(allowedOrigins).toList());
                            corsConf.setAllowedMethods(Collections.singletonList("*"));
                            corsConf.setAllowCredentials(true);
                            corsConf.setAllowedHeaders(Collections.singletonList("*"));
                            corsConf.setExposedHeaders(List.of("Authorization"));
                            corsConf.setMaxAge(360000L);
                            return corsConf;
                        }
                ))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/api/auth/**", "/h2-console/**").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(requestFilter, UsernamePasswordAuthenticationFilter.class)
                // For the H2 console
                .headers(headers -> headers.frameOptions(h -> {Customizer.withDefaults().customize(h.sameOrigin());}))
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEndoder());
        return authenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEndoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConf) throws Exception {
        return authConf.getAuthenticationManager();
    }

}
