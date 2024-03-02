package com.devminds.rentify.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableWebMvc
public class AuthConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(crsf -> crsf.disable())
                .authorizeHttpRequests(
                        auth -> auth
                                .requestMatchers("/rentify/login").permitAll()
                                .requestMatchers("/rentify/register").permitAll()
                                .requestMatchers("/rentify/google-login").permitAll()
                                .requestMatchers("/rentify/categories").permitAll()
                                .requestMatchers("/rentify/categories/*").permitAll()
                                .requestMatchers("/rentify/items").permitAll()
                                .requestMatchers("/rentify/items/**").permitAll()
                                .requestMatchers("/rentify/addresses").permitAll()
                                .requestMatchers("/rentify/addresses/**").permitAll()
                                .requestMatchers("/rentify/favourites/**").permitAll()
                                .requestMatchers("/rentify/favourites/userFavourites/**").permitAll()
                                .requestMatchers("/rentify/users").permitAll()
                                .requestMatchers("/rentify/users/**").permitAll()
                                .requestMatchers("/rentify/images").permitAll()
                                .requestMatchers("/rentify/images/**").permitAll()
                                .requestMatchers("/rentify/views").permitAll()
                                .requestMatchers("/rentify/views/**").permitAll()
                                .requestMatchers("/rentify/likes").permitAll()
                                .requestMatchers("/rentify/likes/**").permitAll()
                                .requestMatchers("/rentify/pictures").permitAll()
                                .requestMatchers("/rentify/pictures/**").permitAll()
                                .requestMatchers("/static/profilePicture.jpg").permitAll()
                                .requestMatchers("/rentify/password/**").permitAll()
                                .requestMatchers("/rentify/verification/**").permitAll()
                                .requestMatchers("/rentify/rents/**").permitAll()
                                .requestMatchers("/v3/**", "/swagger-ui/**").permitAll()
                                .requestMatchers("/rentify/stripe/checkout/{id}").permitAll()
                                .requestMatchers("/rentify/stripe/checkout/webhook").permitAll()
                                .requestMatchers("/rentify/reviews/**").permitAll()
                                .anyRequest().authenticated()

                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
