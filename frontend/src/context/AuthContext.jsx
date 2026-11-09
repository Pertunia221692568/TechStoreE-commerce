import { createContext, useContext, useEffect, useState } from 'react';
import { login as apiLogin, register as apiRegister } from '../services/api';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
    const [user, setUser] = useState(() => {
        try {
            return JSON.parse(localStorage.getItem('techvault_user') || 'null');
        } catch {
            return null;
        }
    });

    const [token, setToken] = useState(() => localStorage.getItem('techvault_token') || null);

    useEffect(() => {
        if (user) localStorage.setItem('techvault_user', JSON.stringify(user));
        else localStorage.removeItem('techvault_user');
    }, [user]);

    useEffect(() => {
        if (token) localStorage.setItem('techvault_token', token);
        else localStorage.removeItem('techvault_token');
    }, [token]);

    const login = async (email, password) => {
        const data = await apiLogin(email, password);
        // data = { token, userId, email, firstName, lastName, role, customerId }
        setToken(data.token);
        setUser({
            id: data.userId,
            email: data.email,
            firstName: data.firstName,
            lastName: data.lastName,
            role: data.role,
            customerId: data.customerId,
        });
        return data;
    };

    const register = async (formData) => {
        const data = await apiRegister(formData);
        setToken(data.token);
        setUser({
            id: data.userId,
            email: data.email,
            firstName: data.firstName,
            lastName: data.lastName,
            role: data.role,
            customerId: data.customerId,
        });
        return data;
    };

    const logout = () => {
        setToken(null);
        setUser(null);
        localStorage.removeItem('techvault_token');
        localStorage.removeItem('techvault_user');
    };

    return (
        <AuthContext.Provider
            value={{
                user,
                token,
                login,
                register,
                logout,
                isAuthenticated: !!token,
                isAdmin: user?.role === 'ADMIN',
            }}
        >
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    const ctx = useContext(AuthContext);
    if (!ctx) throw new Error('useAuth must be used within AuthProvider');
    return ctx;
}