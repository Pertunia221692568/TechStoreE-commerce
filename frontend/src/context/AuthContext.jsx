import { createContext, useContext, useEffect, useState } from 'react';
import {
    login as apiLogin,
    register as apiRegister,
    verifyEmail as apiVerifyEmail,
    forgotPassword as apiForgotPassword,
    resetPassword as apiResetPassword,
} from '../services/api';

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

    const applyAuth = (data) => {
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

    const login = async (email, password) => {
        const data = await apiLogin(email, password);
        return applyAuth(data);
    };

    // Register does NOT log the user in – account is disabled until verified
    const register = async (formData) => {
        const data = await apiRegister(formData);
        if (data.token) applyAuth(data); // verification switched off: logged in straight away
        return data;
        // returns { message, email, userId, customerId, verificationCode, note }
    };

    const verifyEmail = async (email, code) => {
        const data = await apiVerifyEmail(email, code);
        return applyAuth(data); // now we log them in
    };

    const forgotPassword = async (email) => {
        return await apiForgotPassword(email);
    };

    const resetPassword = async (email, code, newPassword) => {
        return await apiResetPassword(email, code, newPassword);
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
                verifyEmail,
                forgotPassword,
                resetPassword,
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
