package com.akash.bawnk.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.akash.bawnk.dto.AccountDto;
import com.akash.bawnk.service.AccountService;
import com.akash.bawnk.service.AdminService;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AccountController {

    private final AccountService accountService;
    private final AdminService adminService;

//    // Get all accounts for the logged-in user
    @GetMapping("/my-accounts")
    public ResponseEntity<List<AccountDto>> getMyAccounts() {
        return ResponseEntity.ok(accountService.getAccountsForCurrentUser());
    }
//
//    // Get details for a single account
    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountDto> getAccountDetails(@PathVariable String accountNumber) {
        return ResponseEntity.ok(accountService.getAccountByNumber(accountNumber));
    }
    
    @GetMapping("/ledger-balance")
    public ResponseEntity<BigDecimal> getLedgerBalance(){
    	
    	BigDecimal totalBalance = adminService.getTotalLedgerBalance();
		return ResponseEntity.ok(totalBalance);
    	
    }
    
//    @PostMapping("/deposit")
//    public ResponseEntity<AccountDto> depositFunds(
//    		@RequestBody DepositDto depositDto
//    		){
//    	AccountDto updatedAccount = accountService.deposit(depositDto);
//    	return ResponseEntity.ok(updatedAccount);
//    }
}