package com.akash.bawnk.service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.akash.bawnk.dto.AccountDto;
import com.akash.bawnk.exception.ResourceNotFoundException;
import com.akash.bawnk.model.Account;
import com.akash.bawnk.model.AccountType;
import com.akash.bawnk.model.User;
import com.akash.bawnk.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService{

	private final AccountRepository accountRepository;
	private static final SecureRandom random = new SecureRandom();
	
	
	@Override
	public void createDefaultAccounts(User user) {
		// create a default current account
		Account checking = Account.builder()
				.user(user)
				.accountNumber(generateUniqueAccountNumber())
				.accountType(AccountType.CURRENT)
				.balance(BigDecimal.valueOf(1000.00))
				.createdAt(LocalDateTime.now())
				.build();
		
		//create a default savings account
		Account savings = Account.builder()
				.user(user)
				.accountNumber(generateUniqueAccountNumber())
				.accountType(AccountType.SAVINGS)
				.balance(BigDecimal.valueOf(5000.00))
				.createdAt(LocalDateTime.now())
				.build();
		
		accountRepository.save(checking);
		accountRepository.save(savings);
		
	}
	
	
	
	@Override
	public List<AccountDto> getAccountsForCurrentUser() {
		User user = getCurrentUser();
		return accountRepository.findByUserId(user.getId())
				.stream()
				.map(this::mapToAccountDto)
				.collect(Collectors.toList());
	}
	

	@Override
	public AccountDto getAccountByNumber(String accountNumber) {
		Account account = accountRepository.findByAccountNumber(accountNumber)
				.orElseThrow(()-> new ResourceNotFoundException("Account not found"));
		
		if(!getCurrentUser().getId().equals(account.getUser().getId()))
			throw new SecurityException("Access Denied");
		
		return mapToAccountDto(account);
	}
	
	private AccountDto mapToAccountDto(Account account) {
		return AccountDto.builder()
				.id(account.getId())
				.accountNumber(account.getAccountNumber())
				.balance(account.getBalance())
				.accountType(account.getAccountType())
				.createdAt(account.getCreatedAt())
				.build();
	}


	private String generateUniqueAccountNumber() {
		return String.valueOf(1000000000L + random.nextInt(900000000));
	}
	
	private User getCurrentUser() {
		// this retrieves the authenticated user object from spring security
		return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}
	
}
