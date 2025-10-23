package com.akash.bawnk.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.akash.bawnk.config.JwtService;
import com.akash.bawnk.dto.AuthResponseDto;
import com.akash.bawnk.dto.LoginDto;
import com.akash.bawnk.dto.RegisterDto;
import com.akash.bawnk.model.Role;
import com.akash.bawnk.model.User;
import com.akash.bawnk.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
	
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;
	private final AccountService accountService;
	
	@Override
	public AuthResponseDto register(RegisterDto registerDto) {
		//create user object
		var user = User.builder()
					.firstName(registerDto.getFirstName())
					.lastName(registerDto.getLastName())
					.username(registerDto.getUsername())
					.email(registerDto.getEmail())
					.password(passwordEncoder.encode(registerDto.getPassword())) //hash the password
					.role(Role.USER)
					.build();
		
		User savedUser = userRepository.save(user);
		
		//create a default account for new user
		accountService.createDefaultAccounts(savedUser);
		
		//generate jwt token
		var jwtToken = jwtService.generateToken(user);
		
		//return token to our DTO
		return AuthResponseDto.builder()
				.token(jwtToken)
				.username(user.getUsername())
				.build();
	}

	@Override
	public AuthResponseDto login(LoginDto loginDto) {
		
		// authenticate the user, throws exception if creds are bad
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						loginDto.getUsername(), 
						loginDto.getPassword()
				)
		);
		
		//if user found we will countinue
		var user = userRepository.findByUsername(loginDto.getUsername())
				.orElseThrow();
		
		//generate token
		var jwtToken = jwtService.generateToken(user);
		
		//return token
		return AuthResponseDto.builder()
				.token(jwtToken)
				.username(user.getUsername())
				.build();
	}
}
