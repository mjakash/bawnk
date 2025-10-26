package com.akash.bawnk.controller;

import java.math.BigDecimal;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.akash.bawnk.dto.AccountDto;
import com.akash.bawnk.dto.LedgerAdjustmentDto;
import com.akash.bawnk.service.AdminService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {
	
	private final AdminService adminService;
	
	@PostMapping("/inject")
	public ResponseEntity<AccountDto> injectFunds(
			@RequestBody
			LedgerAdjustmentDto adjustmentDto
			){
		AccountDto updatedAccount = adminService.injectFunds(adjustmentDto);
		return ResponseEntity.ok(updatedAccount);
	}
	
	@PostMapping("/extract")
    public ResponseEntity<AccountDto> extractFunds(@RequestBody LedgerAdjustmentDto adjustmentDto) {
        AccountDto updatedAccount = adminService.extractFunds(adjustmentDto);
        return ResponseEntity.ok(updatedAccount);
    }
	
	@GetMapping("/ledger-balance") // Make sure this mapping is correct
    public ResponseEntity<BigDecimal> getLedgerBalance() {
        BigDecimal totalBalance = adminService.getTotalLedgerBalance();
        return ResponseEntity.ok(totalBalance);
    }

}
