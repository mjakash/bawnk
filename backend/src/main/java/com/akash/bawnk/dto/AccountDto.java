package com.akash.bawnk.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.akash.bawnk.model.AccountType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountDto {

	private Long id;
	private String accountNumber;
	private BigDecimal balance;
	private AccountType accountType;
	private LocalDateTime createdAt;
	
}
