import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import ErrorPage from '../../pages/ErrorPage';

// Only lets a logged-in user with role ADMIN through.
// Not signed in -> admin sign-in page. Signed in as a customer -> 403 Access denied.
export default function RequireAdmin() {
    const { isAuthenticated, isAdmin } = useAuth();
    const location = useLocation();

    if (!isAuthenticated) {
        return <Navigate to="/admin/login" replace state={{ from: location.pathname }} />;
    }
    if (!isAdmin) {
        return (
            <ErrorPage
                code="403"
                title="Access denied"
                message="This area is for administrators only. You are signed in as a customer."
                showAdminLogin
            />
        );
    }
    return <Outlet />;
}