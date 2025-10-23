import React, { useState, useEffect, useCallback } from 'react';
import { useAuth } from '../context/AuthContext';
import { getMyAccounts } from '../services/accountService';
import AccountList from '../components/AccountList';
import TransferForm from '../components/TransferForm'; // <-- Import TransferForm

// ... (styles remain the same) ...
const dashboardStyles = {
  padding: '2rem',
  fontFamily: 'Arial, sans-serif',
  maxWidth: '1200px',
  margin: '0 auto',
};

const headerStyles = {
  display: 'flex',
  justifyContent: 'space-between',
  alignItems: 'center',
  borderBottom: '2px solid #eee',
  paddingBottom: '1rem',
};

const logoutButtonStyles = {
  padding: '0.5rem 1rem',
  border: 'none',
  borderRadius: '4px',
  backgroundColor: '#d93025',
  color: 'white',
  fontSize: '0.9rem',
  cursor: 'pointer',
};

// --- NEW: Layout styles for dashboard ---
const dashboardLayoutStyles = {
  display: 'grid',
  gridTemplateColumns: '1fr', // Single column on mobile
  gap: '2rem',
};

// --- NEW: Media query for wider screens ---
// We'll apply this inline for simplicity
const desktopLayoutStyles = {
  ...dashboardLayoutStyles,
  gridTemplateColumns: '1fr 1fr', // Two columns on desktop
};


function DashboardPage() {
  const { user, logout } = useAuth();
  
  const [accounts, setAccounts] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');

  // --- MODIFIED: Wrap fetchAccounts in useCallback ---
  // This ensures the function identity is stable and can be used in useEffect
  const fetchAccounts = useCallback(async () => {
    try {
      setIsLoading(true);
      setError('');
      const data = await getMyAccounts();
      setAccounts(data);
    } catch (err) {
      setError('Failed to fetch accounts. Please try again later.');
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  }, []); // No dependencies, so this function is created once

  // Fetch accounts on initial load
  useEffect(() => {
    fetchAccounts();
  }, [fetchAccounts]); // Now depends on the stable fetchAccounts function

  // --- NEW: Callback function for the transfer form ---
  const handleTransferSuccess = () => {
    // Re-fetch accounts to show updated balances
    fetchAccounts();
  };

  // --- NEW: Simple check for window width ---
  // In a real app, you'd use a resize listener hook
  const isDesktop = window.innerWidth > 900; 

  const renderContent = () => {
    if (isLoading) {
      return <p>Loading your accounts...</p>;
    }
    if (error) {
      return <p style={{ color: 'red' }}>{error}</p>;
    }
    return (
      // --- NEW: Apply layout ---
      <div style={isDesktop ? desktopLayoutStyles : dashboardLayoutStyles}>
        <div>
          <AccountList accounts={accounts} />
        </div>
        <div>
          <TransferForm 
            accounts={accounts} 
            onTransferSuccess={handleTransferSuccess} 
          />
        </div>
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
        {renderContent()}
      </main>
    </div>
  );
}

export default DashboardPage;