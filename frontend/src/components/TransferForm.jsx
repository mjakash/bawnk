import React, { useState, useEffect } from 'react'; // <-- Import useEffect
import { transferFunds } from '../services/accountService';
import './TransferForm.css';

function TransferForm({ accounts, onTransferSuccess }) {
  const [fromAccount, setFromAccount] = useState(accounts[0]?.accountNumber || '');
  const [toAccount, setToAccount] = useState('');
  const [amount, setAmount] = useState('');
  const [description, setDescription] = useState('');
  
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  /**
   * NEW: Add the same currency formatter here
   */
  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
    }).format(amount);
  };

  // Set default "from" account when accounts load
  // We use useEffect to handle this
  useEffect(() => {
    if (accounts.length > 0 && !fromAccount) {
      setFromAccount(accounts[0].accountNumber);
    }
  }, [accounts, fromAccount]); // Re-run if accounts or fromAccount changes

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    setIsSubmitting(true);

    try {
      await transferFunds({
        fromAccountNumber: fromAccount,
        toAccountNumber: toAccount,
        amount: parseFloat(amount),
        description: description,
      });

      setSuccess('Transfer successful!');
      // Clear the form
      setToAccount('');
      setAmount('');
      setDescription('');
      // Call the callback to refresh data on the dashboard
      onTransferSuccess(); 
    } catch (err) {
      setError(err.message || 'Transfer failed. Please check the details.');
      console.error(err);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="transfer-form-container">
      <h3>Make a Transfer</h3>
      <form className="transfer-form" onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="fromAccount">From Account</label>
          <select
            id="fromAccount"
            value={fromAccount}
            onChange={(e) => setFromAccount(e.target.value)}
          >
            {accounts.map((acc) => (
              <option key={acc.id} value={acc.accountNumber}>
                {/* --- THIS IS THE FIX --- */}
                {acc.accountType} (Balance: {formatCurrency(acc.balance)})
              </option>
            ))}
          </select>
        </div>

        <div className="form-group">
          <label htmlFor="toAccount">To Account Number</label>
          <input
            id="toAccount"
            type="text"
            value={toAccount}
            onChange={(e) => setToAccount(e.target.value)}
            placeholder="Recipient's account number"
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="amount">Amount (₹)</label> {/* <-- Also updated label */}
          <input
            id="amount"
            type="number"
            value={amount}
            onChange={(e) => setAmount(e.target.value)}
            placeholder="0.00"
            min="0.01"
            step="0.01"
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="description">Description (Optional)</label>
          <input
            id="description"
            type="text"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            placeholder="e.g., Rent"
          />
        </div>

        <div className="form-group span-2">
          <button type="submit" className="transfer-button" disabled={isSubmitting}>
            {isSubmitting ? 'Sending...' : 'Send Transfer'}
          </button>
        </div>
      </form>

      {/* Status Messages */}
      {success && <div className="transfer-status success">{success}</div>}
      {error && <div className="transfer-status error">{error}</div>}
    </div>
  );
}

export default TransferForm;