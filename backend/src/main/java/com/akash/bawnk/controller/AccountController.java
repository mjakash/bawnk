package com.akash.bawnk.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.akash.bawnk.dto.AccountDto;
import com.akash.bawnk.service.AccountService;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    // Get all accounts for the logged-in user
    @GetMapping("/my-accounts")
    public ResponseEntity<List<AccountDto>> getMyAccounts() {
        return ResponseEntity.ok(accountService.getAccountsForCurrentUser());
    }

    // Get details for a single account
    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountDto> getAccountDetails(@PathVariable String accountNumber) {
        return ResponseEntity.ok(accountService.getAccountByNumber(accountNumber));
    }
}