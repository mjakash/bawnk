package com.akash.bawnk.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.akash.bawnk.dto.AccountDto;
import com.akash.bawnk.dto.LedgerAdjustmentDto;
import com.akash.bawnk.exception.ResourceNotFoundException;
import com.akash.bawnk.model.Account;
import com.akash.bawnk.model.AccountType;
import com.akash.bawnk.model.Ledger;
import com.akash.bawnk.model.Role;
import com.akash.bawnk.model.Transaction;
import com.akash.bawnk.model.User;
import com.akash.bawnk.repository.AccountRepository;
import com.akash.bawnk.repository.LedgerRepository;
import com.akash.bawnk.repository.TransactionRepository;
import com.akash.bawnk.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
	
	private final AccountRepository accountRepository;
	private final LedgerRepository ledgerRepository;
	private final TransactionRepository transactionRepository;
	private final AccountService accountService;
	private final UserRepository userRepository;

	@Override
	@Transactional
	public AccountDto injectFunds(LedgerAdjustmentDto adjustmentDto) {
		
		User adminUser = getCurrentAdminUser();
		
		Account adminAccount = accountRepository
				.findByUserId(adminUser.getId())
				.stream()
				.filter(acc->acc.getAccountType() == AccountType.CURRENT)
				.findFirst()
				.orElseThrow(()-> new IllegalArgumentException("Admin user does not have a checking account."));
		
		if(null == adjustmentDto.getAmount() || adjustmentDto.getAmount().compareTo(BigDecimal.ZERO)<=0)
			throw new IllegalArgumentException("Injection amount must be positive");

		Ledger ledger = ledgerRepository.findByLedgerName("SYSTEM_TOTAL")
				.orElseThrow(()-> new IllegalStateException("Central ledger not found!"));
		
		adminAccount.setBalance(adminAccount.getBalance().add(adjustmentDto.getAmount()));
		Account savedAccount = accountRepository.save(adminAccount);
		
		ledger.setTotalBalance(ledger.getTotalBalance().add(adjustmentDto.getAmount()));
		ledgerRepository.save(ledger);
		
		Transaction injectionTx = Transaction.builder()
				.toAccount(savedAccount)
				.amount(adjustmentDto.getAmount())
				.description("ADMIN INJECTION: "+adjustmentDto.getDescription())
				.timestamp(LocalDateTime.now())
				.build();
		transactionRepository.save(injectionTx);
		
		return mapToDto(savedAccount);
		
	}


	@Override
	public AccountDto extractFunds(LedgerAdjustmentDto adjustmentDto) {
		
		User adminUser = getCurrentAdminUser();
		
		Account adminAccount = accountRepository.findByUserId(adminUser.getId())
				.stream()
				.filter(acc->acc.getAccountType() == AccountType.CURRENT)
				.findFirst()
				.orElseThrow(()->new IllegalStateException("Admin user does not have a current account"));
		
		if(null==adjustmentDto.getAmount() || adjustmentDto.getAmount().compareTo(BigDecimal.ZERO)<=0)
			throw new IllegalArgumentException("Extraction amount must be positive");
		
		if(adminAccount.getBalance().compareTo(adjustmentDto.getAmount())<0)
			throw new IllegalArgumentException("Insufficient funds in the admin's account");
		
		Ledger ledger = ledgerRepository.findByLedgerName("SYSTEM_TOTAL")
				.orElseThrow(()-> new IllegalStateException("Central Ledger not found!"));

		if (ledger.getTotalBalance().compareTo(adjustmentDto.getAmount()) < 0) {
            throw new IllegalStateException("Insufficient funds in the central ledger - data inconsistency detected!");
        }

        // 7. Update admin's account balance
        adminAccount.setBalance(adminAccount.getBalance().subtract(adjustmentDto.getAmount()));
        Account savedAccount = accountRepository.save(adminAccount);

        // 8. Update ledger balance
        ledger.setTotalBalance(ledger.getTotalBalance().subtract(adjustmentDto.getAmount()));
        ledgerRepository.save(ledger);

        // 9. Log the transaction
        Transaction extractionTx = Transaction.builder()
                .fromAccount(savedAccount) // From Admin's account
                .amount(adjustmentDto.getAmount().negate())
                .description("ADMIN EXTRACTION: " + adjustmentDto.getDescription())
                .timestamp(LocalDateTime.now())
                .build();
        transactionRepository.save(extractionTx);
		
		return mapToDto(savedAccount);
	}
	
	private User getCurrentAdminUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof User && ((User) principal).getRole() == Role.ADMIN) {
            return (User) principal;
        }
        // This should not happen if @PreAuthorize("hasRole('ADMIN')") is working
        throw new SecurityException("Authenticated user is not an ADMIN or not found.");
    }
	
	private AccountDto mapToDto(Account account) {
		return AccountDto.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .accountType(account.getAccountType())
                .createdAt(account.getCreatedAt())
                .build();
	}


	@Override
	public BigDecimal getTotalLedgerBalance() {
		Ledger ledger = ledgerRepository.findByLedgerName("SYSTEM_TOTAL")
				.orElseThrow(()-> new IllegalStateException("Central ledger not found!"));
		
		return ledger.getTotalBalance();
	}

}
