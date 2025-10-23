package com.akash.bawnk.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.akash.bawnk.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
	
	//custom query to find all transactions for a given account ID
	@Query("SELECT t FROM Transaction t "
			+ "WHERE t.fromAccount.id = :accountId "
			+ "OR t.toAccount.id = :accountId "
			+ "ORDER BY t.timestamp DESC")
	List<Transaction> findTransactionsByAccountId(Long accountId);

}
