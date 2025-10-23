package com.akash.bawnk.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class TransferRequestDto {

	private String fromAccountNumber;
	private String toAccountNumber;
	private BigDecimal amount;
	private String description;
	
}
