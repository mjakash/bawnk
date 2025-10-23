package com.akash.bawnk.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthFilter jwtAuthFilter;
	private final AuthenticationProvider authenticationProvider;
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
		
		http
		
		//disable Cross-site request forgery
		.csrf(csrf-> csrf.disable())
		
		//define public and private endpoints
		.authorizeHttpRequests(auth-> auth
				.requestMatchers("api/auth/**").permitAll() //allow all requests to /api/auth
				.anyRequest().authenticated() //all other requests must be authenticated 
		)
		
		//stateless session
		.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
		
		//custom authentication provider
		
		.authenticationProvider(authenticationProvider)
		
		//add our custom jwt filter before the standard username/password filter
		
		.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
		
		return http.build();
	}
	
}
