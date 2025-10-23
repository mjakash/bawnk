package com.akash.bawnk.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.akash.bawnk.dto.TransactionDto;
import com.akash.bawnk.dto.TransferRequestDto;
import com.akash.bawnk.service.TransactionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

	private final TransactionService transactionService;
	
	//perform a new transfer
	@PostMapping("/transfer")
	public ResponseEntity<Void> performTransfer(
			@RequestBody TransferRequestDto transferRequestDto
	){
		transactionService.performTransfer(transferRequestDto);
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/{accountNumber}/history")
	public ResponseEntity<List<TransactionDto>> getTransactionHistory(
			@PathVariable String accountNumber){
		return ResponseEntity.ok(transactionService.getTransactionsForAccount(accountNumber));
	}
	
}
