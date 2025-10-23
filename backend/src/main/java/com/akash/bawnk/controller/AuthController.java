package com.akash.bawnk.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.akash.bawnk.dto.AuthResponseDto;
import com.akash.bawnk.dto.LoginDto;
import com.akash.bawnk.dto.RegisterDto;
import com.akash.bawnk.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService; 
	
	@PostMapping("/register")
	public ResponseEntity<AuthResponseDto> register(
				@RequestBody RegisterDto registerDto
	){
		return ResponseEntity.ok(authService.register(registerDto));
	}
	
	@PostMapping("/login")
	public ResponseEntity<AuthResponseDto> login(
				@RequestBody LoginDto loginDto
	){
		return ResponseEntity.ok(authService.login(loginDto));
	}
	
}
