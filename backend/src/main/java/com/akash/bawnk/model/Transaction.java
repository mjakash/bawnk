package com.akash.bawnk.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.ManyToAny;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="transaction_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private BigDecimal amount;
	
	@Column(nullable = false)
	private LocalDateTime timestamp;
	
	@Column(nullable = false)
	private String description;
	
	//many transaction can come from one account
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "from_account_id")
	private Account fromAccount;
	
	//many transaction can come to one account
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "to_account_id")
	private Account toAccount;
	
	@PrePersist
	protected void onPersist() {
		this.timestamp = LocalDateTime.now();
	}
	
}
