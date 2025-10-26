import React, { useState, useEffect, useCallback } from 'react';
import { useAuth } from '../context/AuthContext';
import { getMyAccounts, getTransactionHistory } from '../services/accountService'; // <-- Import getTransactionHistory
import AccountList from '../components/AccountList';
import TransferForm from '../components/TransferForm';
import TransactionHistory from '../components/TransactionHistory'; // <-- Import TransactionHistory

// --- Styles (keep existing styles) ---
const dashboardStyles = { /* ... */ };
const headerStyles = { /* ... */ };
const logoutButtonStyles = { /* ... */ };
const dashboardLayoutStyles = { /* ... */ };
const desktopLayoutStyles = { /* ... */ };

// --- NEW: Styles for account selector ---
const accountSelectorStyles = {
  marginBottom: '1rem',
  padding: '0.5rem',
  fontSize: '1rem',
  borderRadius: '4px',
  border: '1px solid #ccc',
  minWidth: '250px'
};


function DashboardPage() {
  const { user, logout } = useAuth();

  // Account state
  const [accounts, setAccounts] = useState([]);
  const [isLoadingAccounts, setIsLoadingAccounts] = useState(true);
  const [accountsError, setAccountsError] = useState('');

  // --- NEW: State for selected account and its transactions ---
  const [selectedAccount, setSelectedAccount] = useState(null); // Holds the full account object
  const [transactions, setTransactions] = useState([]);
  const [isLoadingTransactions, setIsLoadingTransactions] = useState(false);
  const [transactionsError, setTransactionsError] = useState('');

  // Fetch accounts on initial load
  const fetchAccounts = useCallback(async () => {
    try {
      setIsLoadingAccounts(true);
      setAccountsError('');
      const data = await getMyAccounts();
      setAccounts(data);
      // Automatically select the first account if none is selected
      if (data.length > 0 && !selectedAccount) {
        setSelectedAccount(data[0]);
      }
    } catch (err) {
      setAccountsError('Failed to fetch accounts. Please try again later.');
      console.error(err);
    } finally {
      setIsLoadingAccounts(false);
    }
  }, [selectedAccount]); // Re-run if selectedAccount changes (to ensure it's still valid)

  // Fetch transactions when the selected account changes
  useEffect(() => {
    const fetchTransactions = async () => {
      if (!selectedAccount) return; // Don't fetch if no account is selected

      try {
        setIsLoadingTransactions(true);
        setTransactionsError('');
        const history = await getTransactionHistory(selectedAccount.accountNumber);
        setTransactions(history);
      } catch (err) {
        setTransactionsError(`Failed to fetch transactions for ${selectedAccount.accountNumber}.`);
        console.error(err);
      } finally {
        setIsLoadingTransactions(false);
      }
    };

    fetchTransactions();
  }, [selectedAccount]); // Dependency: re-fetch when selectedAccount changes

  // Initial fetch of accounts
  useEffect(() => {
    fetchAccounts();
  }, [fetchAccounts]);

  // Callback for successful transfer - refresh accounts AND transactions
  const handleTransferSuccess = () => {
    fetchAccounts(); // This will update balances and potentially re-select first account
    // Re-fetch transactions for the currently selected account after a short delay
    // to allow backend balance update to reflect if necessary
    setTimeout(() => {
       if (selectedAccount) {
           const fetchTransactions = async () => {
               try {
                   setIsLoadingTransactions(true);
                   setTransactionsError('');
                   const history = await getTransactionHistory(selectedAccount.accountNumber);
                   setTransactions(history);
               } catch (err) {
                   setTransactionsError(`Failed to fetch transactions for ${selectedAccount.accountNumber}.`);
                   console.error(err);
               } finally {
                   setIsLoadingTransactions(false);
               }
           };
           fetchTransactions();
       }
    }, 500); // 500ms delay
  };

  // Handle changing the selected account from dropdown
  const handleAccountChange = (event) => {
    const selectedNumber = event.target.value;
    const account = accounts.find(acc => acc.accountNumber === selectedNumber);
    setSelectedAccount(account);
  };

  // Simple check for window width
  const isDesktop = window.innerWidth > 900;

  // Render accounts section
  const renderAccountsSection = () => {
    if (isLoadingAccounts) return <p>Loading accounts...</p>;
    if (accountsError) return <p style={{ color: 'red' }}>{accountsError}</p>;
    return (
      <>
        <AccountList accounts={accounts} />
        <TransferForm
          accounts={accounts}
          onTransferSuccess={handleTransferSuccess}
        />
      </>
    );
  };

  // Render transactions section
  const renderTransactionsSection = () => {
    return (
      <div>
        <h3>View Transactions</h3>
        <select
          value={selectedAccount?.accountNumber || ''}
          onChange={handleAccountChange}
          disabled={accounts.length === 0 || isLoadingAccounts}
          style={accountSelectorStyles}
        >
          {accounts.length === 0 && <option>No accounts available</option>}
          {accounts.map(acc => (
            <option key={acc.id} value={acc.accountNumber}>
              {acc.accountType} - ****{acc.accountNumber.slice(-4)}
            </option>
          ))}
        </select>

        {isLoadingTransactions && <p>Loading transactions...</p>}
        {transactionsError && <p style={{ color: 'red' }}>{transactionsError}</p>}
        {!isLoadingTransactions && !transactionsError && selectedAccount && (
          <TransactionHistory
            transactions={transactions}
            selectedAccountNumber={selectedAccount.accountNumber}
          />
        )}
      </div>
    );
  };

  return (
    <div style={dashboardStyles}>
      <header style={headerStyles}>
        <h2>Bawnk Dashboard</h2>
        <div>
          <span>Welcome, <b>{user?.username}</b>!</span>
          <button onClick={logout} style={logoutButtonStyles}>
            Logout
          </button>
        </div>
      </header>

      <main>
        <div style={isDesktop ? desktopLayoutStyles : dashboardLayoutStyles}>
          <div>
            {renderAccountsSection()}
          </div>
          <div>
            {renderTransactionsSection()}
          </div>
        </div>
      </main>
    </div>
  );
}

export default DashboardPage;