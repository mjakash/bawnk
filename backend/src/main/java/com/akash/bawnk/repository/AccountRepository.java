package com.akash.bawnk.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.akash.bawnk.model.Account;

public interface AccountRepository extends JpaRepository<Account, Long>{

	//find all acounts belonging to the specific userID
	List<Account> findByUserId(Long userId);
	
	//find a single account by its number
	Optional<Account> findByAccountNumber(String accountNumber);
	
}
