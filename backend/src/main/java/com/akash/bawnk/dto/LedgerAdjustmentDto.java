package com.akash.bawnk.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class LedgerAdjustmentDto {
//	private String targetAccountNumber;
	private BigDecimal amount;
	private String description;
}
