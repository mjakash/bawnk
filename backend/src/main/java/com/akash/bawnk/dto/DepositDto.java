package com.akash.bawnk.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class DepositDto {

	private String accountNumber;
	private BigDecimal amount;
	private String description;
}
