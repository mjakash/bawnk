import React, { createContext, useState, useContext } from 'react';
import api from '../services/api.js'; // Our new api service

// 1. Create the Context
const AuthContext = createContext(null);

// 2. Create the Provider Component
export function AuthProvider({ children }) {
  // 3. Initialize state from localStorage
  const [token, setToken] = useState(() => localStorage.getItem('token'));
  const [user, setUser] = useState(() => {
    const storedUser = localStorage.getItem('user');
    return storedUser ? JSON.parse(storedUser) : null;
  });

  const isAuthenticated = !!token;

  // 4. Login Function
  const login = async (username, password) => {
    try {
      const response = await api.post('/api/auth/login', { username, password });
      const { token, username: loggedInUsername } = response.data;
      const userObject = { username: loggedInUsername };

      // Update state
      setToken(token);
      setUser(userObject);

      // Update localStorage
      localStorage.setItem('token', token);
      localStorage.setItem('user', JSON.stringify(userObject));
      
    } catch (error) {
      console.error("Login failed:", error);
      // We'll handle showing this error to the user in the login form
      throw error; 
    }
  };

  // 5. Register Function
  const register = async (registerData) => {
    try {
      const response = await api.post('/api/auth/register', registerData);
      const { token, username: newUsername } = response.data;
      const userObject = { username: newUsername };

      // Update state
      setToken(token);
      setUser(userObject);

      // Update localStorage
      localStorage.setItem('token', token);
      localStorage.setItem('user', JSON.stringify(userObject));
      
    } catch (error) {
      console.error("Registration failed:", error);
      throw error;
    }
  };

  // 6. Logout Function
  const logout = () => {
    // Clear state
    setToken(null);
    setUser(null);

    // Clear localStorage
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  };

  // 7. The value to be passed to consuming components
  const value = {
    token,
    user,
    isAuthenticated,
    login,
    register,
    logout,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

// 8. Custom Hook (to easily consume the context)
export const useAuth = () => {
  return useContext(AuthContext);
};