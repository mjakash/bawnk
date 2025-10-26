import React from 'react';
import './TransactionHistory.css'; // Import the styles

function TransactionHistory({ transactions, selectedAccountNumber }) {

  /**
   * Formatter for currency (Rupee)
   */
  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
    }).format(amount);
  };

  /**
   * Formatter for date and time
   * Handles potential invalid date strings gracefully.
   */
  const formatDateTime = (isoString) => {
    if (!isoString) return 'N/A';
    try {
      const date = new Date(isoString);
      // Check if the date is valid after parsing
      if (isNaN(date.getTime())) {
          console.warn("Invalid date string received:", isoString);
          return isoString; // Return original string if invalid
      }
      return date.toLocaleString('en-IN', {
        day: '2-digit',    // e.g., 01, 26
        month: 'short',    // e.g., Jan, Oct
        year: 'numeric',   // e.g., 2025
        hour: 'numeric',   // e.g., 1, 12
        minute: '2-digit', // e.g., 05, 59
        hour12: true       // Use AM/PM
      });
    } catch (e) {
      console.error("Error formatting date:", e);
      return isoString; // Fallback to original string on error
    }
  };

  // Optional: Add console log here during debugging
  // console.log("Transactions received by component:", transactions);

  return (
    <div className="transaction-history">
      <h4>Transaction History</h4>
      {/* Check if transactions is actually an array before mapping */}
      {!Array.isArray(transactions) || transactions.length === 0 ? (
        <p className="no-transactions">No transactions found for this account.</p>
      ) : (
        <table className="transaction-table">
          <thead>
            <tr>
              <th>Date</th>
              <th>Description</th>
              <th>Amount</th>
              <th>From/To</th>
            </tr>
          </thead>
          <tbody>
            {transactions.map((tx) => {
              // Ensure tx object exists and has necessary properties
              if (!tx || typeof tx !== 'object') {
                console.warn("Invalid transaction object:", tx);
                return null; // Skip rendering this row if tx is invalid
              }

              // Determine if it's a debit or credit relative to the selected account
              const isDebit = tx.fromAccountNumber === selectedAccountNumber;
              const amountClass = isDebit ? 'amount-debit' : 'amount-credit';
              
              // Handle counterparty display, defaulting gracefully if needed
              let counterParty = 'N/A';
              if (isDebit && tx.toAccountNumber && tx.toAccountNumber !== 'N/A') {
                  counterParty = `To: ...${String(tx.toAccountNumber).slice(-4)}`;
              } else if (!isDebit && tx.fromAccountNumber && tx.fromAccountNumber !== 'N/A') {
                  counterParty = `From: ...${String(tx.fromAccountNumber).slice(-4)}`;
              } else if (!isDebit) {
                  // Likely a deposit or admin injection if 'from' is N/A/null
                  counterParty = 'Deposit/Credit';
              } else if (isDebit && (!tx.toAccountNumber || tx.toAccountNumber === 'N/A')) {
                  // Likely an admin extraction if 'to' is N/A/null
                   counterParty = 'Withdrawal/Debit';
              }

              // Ensure amount is a number before formatting
              const amountValue = typeof tx.amount === 'number' ? tx.amount : 0;

              return (
                <tr key={tx.id || Math.random()}> {/* Use random key as fallback if ID is missing */}
                  <td>{formatDateTime(tx.timestamp)}</td>
                  <td>{tx.description || 'No Description'}</td>
                  <td className={amountClass}>
                    {/* Add sign based on debit/credit */}
                    {isDebit ? '-' : '+'} {formatCurrency(Math.abs(amountValue))}
                  </td>
                  <td>{counterParty}</td>
                </tr>
              );
            })}
          </tbody>
        </table>
      )}
    </div>
  );
}

export default TransactionHistory;