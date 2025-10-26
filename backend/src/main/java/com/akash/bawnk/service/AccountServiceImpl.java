package com.akash.bawnk.service; // Use your actual package name

import com.akash.bawnk.dto.AccountDto;
import com.akash.bawnk.exception.ResourceNotFoundException;
import com.akash.bawnk.model.Account;
import com.akash.bawnk.model.AccountType;
import com.akash.bawnk.model.User;
import com.akash.bawnk.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private static final SecureRandom random = new SecureRandom(); // For generating account numbers

    @Override
    public void createDefaultAccounts(User user) {
        // Create a default Checking account with ZERO balance
        Account checking = Account.builder()
                .user(user)
                .accountNumber(generateUniqueAccountNumber())
                .accountType(AccountType.CURRENT)
                .balance(BigDecimal.ZERO) // Initial balance is Zero
                .createdAt(LocalDateTime.now())
                .build();

        // Create a default Savings account with ZERO balance
        Account savings = Account.builder()
                .user(user)
                .accountNumber(generateUniqueAccountNumber())
                .accountType(AccountType.SAVINGS)
                .balance(BigDecimal.ZERO) // Initial balance is Zero
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
                .map(this::mapToAccountDto) // Use helper to convert Entity to DTO
                .collect(Collectors.toList());
    }

    @Override
    public AccountDto getAccountByNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with number: " + accountNumber));

        // Security Check: Ensure the requested account belongs to the logged-in user
        if (!account.getUser().getId().equals(getCurrentUser().getId())) {
            // In a real app, log this attempt. Throwing generic error for security.
            throw new ResourceNotFoundException("Account not found");
            // Or throw new SecurityException("Access denied");
        }

        return mapToAccountDto(account);
    }

    // The deposit method was intentionally removed

    // --- Helper Methods ---

    /**
     * Retrieves the currently authenticated User object from Spring Security context.
     * @return The authenticated User.
     * @throws ClassCastException if the principal is not of type User.
     * @throws org.springframework.security.core.AuthenticationException if no user is authenticated.
     */
    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof User) {
            return (User) principal;
        }
        // This should ideally not happen if the endpoint is secured correctly
        // Handle appropriately, maybe throw an exception or return null based on needs
        throw new IllegalStateException("Authenticated principal is not an instance of User");
    }

    /**
     * Generates a pseudo-unique 10-digit account number.
     * Note: In a production system, a more robust uniqueness check (e.g., database constraint
     * or a dedicated sequence generator) would be necessary.
     * @return A String representing the 10-digit account number.
     */
    private String generateUniqueAccountNumber() {
        // Generates a number between 1,000,000,000 and 1,899,999,999
        long min = 1_000_000_000L;
        long max = 1_900_000_000L; // Limit range slightly for simplicity
        long randomNum = min + ((long) (random.nextDouble() * (max - min)));
        return String.valueOf(randomNum);
        // Consider adding a loop with existsByAccountNumber check for production
    }

    /**
     * Maps an Account entity object to an AccountDto object.
     * @param account The Account entity.
     * @return The corresponding AccountDto.
     */
    private AccountDto mapToAccountDto(Account account) {
        return AccountDto.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .accountType(account.getAccountType())
                .createdAt(account.getCreatedAt())
                .build();
    }
}