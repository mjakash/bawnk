package com.akash.bawnk.service;

import java.util.List;

import com.akash.bawnk.dto.AccountDto;
import com.akash.bawnk.model.User;

public interface AccountService {

	//creates CURRENT and SAVINGS accounts for new user
	void createDefaultAccounts(User user);
	
	List<AccountDto> getAccountsForCurrentUser();
	
	AccountDto getAccountByNumber(String accountNumber);
	
}
