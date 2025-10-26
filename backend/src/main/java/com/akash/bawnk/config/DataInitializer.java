package com.akash.bawnk.config;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.akash.bawnk.model.Ledger;
import com.akash.bawnk.model.Role;
import com.akash.bawnk.model.User;
import com.akash.bawnk.repository.LedgerRepository;
import com.akash.bawnk.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner{

	private final LedgerRepository ledgerRepository;
	private final UserRepository userRepository;
	
	@Override
	public void run(String... args) throws Exception{
		if(ledgerRepository.findByLedgerName("SYSTEM_TOTAL").isEmpty()) {
			Ledger systemLedger = new Ledger(BigDecimal.ZERO);
			ledgerRepository.save(systemLedger);
		}
		
		List<User> admins = userRepository.findByRole(Role.ADMIN);
		
		if(admins.size()>1)
			throw new IllegalStateException("FATAL: More than one ADMIN user found in the system");
		
	}
	
}
