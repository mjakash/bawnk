import React, { useState } from 'react';
import './AccountList.css'; // Import the styles

function AccountList({ accounts }) {
  // NEW: State to track visibility of each account number
  // It's an object where keys are account IDs, e.g., { 1: false, 2: true }
  const [visibility, setVisibility] = useState({});

  /**
   * NEW: Toggles the visibility for a specific account ID
   */
  const toggleVisibility = (accountId) => {
    setVisibility((prevVisibility) => ({
      ...prevVisibility,
      [accountId]: !prevVisibility[accountId], // Toggle the boolean value
    }));
  };

  /**
   * UPDATED: Format currency to INR (Indian Rupee)
   */
  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('en-IN', { // <-- Changed locale
      style: 'currency',
      currency: 'INR', // <-- Changed currency
    }).format(amount);
  };

  /**
   * NEW: Helper to get the display text for the account number
   */
  const getAccountNumberText = (account) => {
    const isVisible = !!visibility[account.id];
    if (isVisible) {
      return account.accountNumber;
    }
    return `**** **** ${account.accountNumber.slice(-4)}`;
  };

  return (
    <div className="account-list">
      <h3>Your Accounts</h3>
      {accounts.length === 0 ? (
        <p>You have no accounts.</p>
      ) : (
        accounts.map((account) => {
          const isVisible = !!visibility[account.id];
          return (
            <div key={account.id} className="account-card">
              <div className="account-header">
                <span className="account-type">{account.accountType}</span>
                
                {/* UPDATED: Account number with toggle button */}
                <div className="account-number">
                  <span>{getAccountNumberText(account)}</span>
                  <button
                    onClick={() => toggleVisibility(account.id)}
                    className="visibility-toggle"
                    title={isVisible ? 'Hide number' : 'Show number'}
                  >
                    {/* Simple eye emoji. You can replace with an icon library later. */}
                    {isVisible ? '🙈' : '👁️'}
                  </button>
                </div>
              </div>
              
              <div>
                <div className="account-balance-label">Balance</div>
                <div className="account-balance">{formatCurrency(account.balance)}</div>
              </div>
            </div>
          );
        })
      )}
    </div>
  );
}

export default AccountList;