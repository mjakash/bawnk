package com.akash.bawnk.repository;

import com.akash.bawnk.model.Ledger;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;

public interface LedgerRepository extends JpaRepository<Ledger, Long> {
	
	@Transactional
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<Ledger> findByLedgerName(String ledgerName);
}
