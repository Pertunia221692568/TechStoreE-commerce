import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

// Only lets a logged-in user with role ADMIN through.
export default function RequireAdmin() {
    const { isAuthenticated, isAdmin } = useAuth();
    const location = useLocation();

    if (!isAuthenticated || !isAdmin) {
        return <Navigate to="/admin/login" replace state={{ from: location.pathname }} />;
    }
    return <Outlet />;
}
