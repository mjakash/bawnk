package com.akash.bawnk.service;

import java.util.List;

import com.akash.bawnk.dto.TransactionDto;
import com.akash.bawnk.dto.TransferRequestDto;

public interface TransactionService {
	
	void performTransfer(TransferRequestDto transferRequestDto);
	
	List<TransactionDto> getTransactionsForAccount(String accountNumber);
}
