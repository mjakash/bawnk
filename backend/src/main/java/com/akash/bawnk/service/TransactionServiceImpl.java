package com.akash.bawnk.service;


import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.akash.bawnk.dto.TransactionDto;
import com.akash.bawnk.dto.TransferRequestDto;
import com.akash.bawnk.exception.ResourceNotFoundException;
import com.akash.bawnk.exception.TransactionFailedException;
import com.akash.bawnk.model.Account;
import com.akash.bawnk.model.Transaction;
import com.akash.bawnk.model.User;
import com.akash.bawnk.repository.AccountRepository;
import com.akash.bawnk.repository.TransactionRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final EntityManager entityManager; // Injected to call the stored procedure

    // Injected from application.properties
    @Value("${application.db.procedures.execute-transfer}")
    private String executeTransferProcedureName;

    @Override
    @Transactional // This annotation wraps the method in a database transaction
    public void performTransfer(TransferRequestDto transferRequestDto) {
        
        // 1. Find the 'from' and 'to' accounts
        Account fromAccount = accountRepository.findByAccountNumber(transferRequestDto.getFromAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Sender account not found"));
        
        Account toAccount = accountRepository.findByAccountNumber(transferRequestDto.getToAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Recipient account not found"));

        // 2. Security Check: Ensure the 'from' account belongs to the logged-in user
        if (!fromAccount.getUser().getId().equals(getCurrentUser().getId())) {
            throw new SecurityException("You do not have permission to transfer from this account.");
        }

        // 3. Call the Stored Procedure
        StoredProcedureQuery query = entityManager
            .createStoredProcedureQuery(executeTransferProcedureName)
            .registerStoredProcedureParameter(1, Long.class, ParameterMode.IN) // p_from_account_id
            .registerStoredProcedureParameter(2, Long.class, ParameterMode.IN) // p_to_account_id
            .registerStoredProcedureParameter(3, java.math.BigDecimal.class, ParameterMode.IN) // p_transfer_amount
            .registerStoredProcedureParameter(4, String.class, ParameterMode.IN) // p_transfer_description
            .registerStoredProcedureParameter(5, Boolean.class, ParameterMode.OUT); // p_success (OUT)

        // 4. Set the parameters
        query.setParameter(1, fromAccount.getId());
        query.setParameter(2, toAccount.getId());
        query.setParameter(3, transferRequestDto.getAmount());
        query.setParameter(4, transferRequestDto.getDescription());

        // 5. Execute the query
        query.execute();

        // 6. Get the result from the stored procedure
        Boolean transferSuccess = (Boolean) query.getOutputParameterValue(5);

        // 7. If the procedure returned false, something went wrong (e.g., insufficient funds)
        if (transferSuccess == null || !transferSuccess) {
            throw new TransactionFailedException("Transaction failed. Check for insufficient funds or invalid accounts.");
        }
        
        // If we reach here, the transaction was successful and is committed by @Transactional.
    }

    @Override
    public List<TransactionDto> getTransactionsForAccount(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        
        // Security Check: Ensure the account belongs to the logged-in user
        if (!account.getUser().getId().equals(getCurrentUser().getId())) {
            throw new SecurityException("Access denied");
        }

        List<Transaction> transactions = transactionRepository.findTransactionsByAccountId(account.getId());
        
        return transactions.stream()
                .map(this::mapToTransactionDto)
                .collect(Collectors.toList());
    }

    // --- Helper Methods ---

    /**
     * Gets the authenticated User object from Spring Security's context.
     */
    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
    
    /**
     * Maps a Transaction Entity to a Transaction DTO.
     */
    private TransactionDto mapToTransactionDto(Transaction transaction) {
        return TransactionDto.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .timestamp(transaction.getTimestamp())
                .description(transaction.getDescription())
                .fromAccountNumber(transaction.getFromAccount() != null ? transaction.getFromAccount().getAccountNumber() : "N/A")
                .toAccountNumber(transaction.getToAccount() != null ? transaction.getToAccount().getAccountNumber() : "N/A")
                .build();
    }
}