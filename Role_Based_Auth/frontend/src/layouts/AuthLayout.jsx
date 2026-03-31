import { Outlet, Navigate } from 'react-router-dom'
import useAuthStore from '../modules/auth/store/auth.store'

export default function AuthLayout() {
  const { isAuthenticated, isLoading, user } = useAuthStore()

  if (isLoading) return <div className="page-loading">Checking session…</div>

  if (isAuthenticated) {
    const isAdmin = user?.roles?.includes('ADMIN')
    return <Navigate to={isAdmin ? '/admin/users' : '/dashboard'} replace />
  }

  return (
    <div className="auth-layout">
      <Outlet />
    </div>
  )
}
