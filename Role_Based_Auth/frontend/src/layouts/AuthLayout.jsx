import { Outlet, Navigate } from 'react-router-dom'
import useAuthStore from '../modules/auth/store/auth.store'

export default function AuthLayout() {
  const { isAuthenticated, isLoading } = useAuthStore()

  if (isLoading) return <div className="page-loading">Checking session…</div>

  if (isAuthenticated) return <Navigate to="/dashboard" replace />

  return (
    <div className="auth-layout">
      <Outlet />
    </div>
  )
}
