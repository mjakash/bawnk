import api from './api'; // Our configured axios instance

/**
 * Fetches all accounts for the currently logged-in user.
 * The token is automatically added by the 'api' instance.
 */
export const getMyAccounts = async () => {
  try {
    const response = await api.get('/api/accounts/my-accounts');
    return response.data; // This will be the list of AccountDto
  } catch (error) {
    console.error("Error fetching accounts:", error);
    throw error; // Re-throw to be caught by the component
  }
};

/**
 * Fetches the transaction history for a specific account number.
 */
export const getTransactionHistory = async (accountNumber) => {
  try {
    const response = await api.get(`/api/transactions/${accountNumber}/history`);
    return response.data; // This will be the list of TransactionDto
  } catch (error) {
    console.error(`Error fetching transactions for ${accountNumber}:`, error);
    throw error;
  }
};

/**
 * Executes a fund transfer.
 * @param {object} transferData - Contains fromAccountNumber, toAccountNumber, amount, description
 */
export const transferFunds = async (transferData) => {
  try {
    // The endpoint is /api/transactions/transfer
    // The body matches our TransferRequestDto
    const response = await api.post('/api/transactions/transfer', transferData);
    return response.data; // Will be empty, but a 200 OK confirms success
  } catch (error) {
    console.error("Error performing transfer:", error);
    // Pass the specific error message from the backend if it exists
    throw new Error(error.response?.data?.message || "Transfer failed");
  }
};