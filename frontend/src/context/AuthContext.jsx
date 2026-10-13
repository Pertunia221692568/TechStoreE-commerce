import { createContext, useContext, useEffect, useState } from 'react';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    try {
      return JSON.parse(localStorage.getItem('techvault_user') || 'null');
    } catch {
      return null;
    }
  });

  useEffect(() => {
    if (user) localStorage.setItem('techvault_user', JSON.stringify(user));
    else localStorage.removeItem('techvault_user');
  }, [user]);

  const login = (customer) => setUser(customer);
  const logout = () => setUser(null);

  return (
    <AuthContext.Provider value={{ user, login, logout, isAuthenticated: !!user }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}
