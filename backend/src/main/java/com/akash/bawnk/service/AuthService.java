package com.akash.bawnk.service;

import com.akash.bawnk.dto.AuthResponseDto;
import com.akash.bawnk.dto.LoginDto;
import com.akash.bawnk.dto.RegisterDto;

public interface AuthService {

	AuthResponseDto register(RegisterDto registerDto);
	AuthResponseDto login(LoginDto loginDto);
}
