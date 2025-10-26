package com.akash.bawnk.service;

import java.math.BigDecimal;

import com.akash.bawnk.dto.AccountDto;
import com.akash.bawnk.dto.LedgerAdjustmentDto;

public interface AdminService {
	AccountDto injectFunds(LedgerAdjustmentDto adjustmentDto);
	AccountDto extractFunds(LedgerAdjustmentDto adjustmentDto);
	BigDecimal getTotalLedgerBalance();
}
