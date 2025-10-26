package com.akash.bawnk.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "central_ledger")
@Data
@NoArgsConstructor
public class Ledger {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, unique = true)
	private String ledgerName = "SYSTEM_TOTAL";
	
	@Column(nullable = false)
	private BigDecimal totalBalance;
	
	public Ledger(BigDecimal initialBalance) {
		this.totalBalance = initialBalance;
	}
	
}
