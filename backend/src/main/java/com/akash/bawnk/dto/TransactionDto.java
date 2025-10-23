package com.akash.bawnk.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionDto {

	private Long id;
	private BigDecimal amount;
	private LocalDateTime timestamp;
	private String description;
	private String fromAccountNumber;
	private String toAccountNumber;
	
}
