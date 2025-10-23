import axios from 'axios';

// Create a new axios instance
const api = axios.create({
  // The baseURL will be proxied by Vite during development
  // and handled by Nginx in production (as per our setup)
  baseURL: '/'
});

/**
 * Request Interceptor
 * * This function is called BEFORE every request is sent.
 * It checks localStorage for the token and adds it to the headers.
 */
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
}, (error) => {
  return Promise.reject(error);
});

export default api;