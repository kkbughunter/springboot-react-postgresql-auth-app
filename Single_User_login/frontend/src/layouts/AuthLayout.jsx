import { Outlet, Navigate } from 'react-router-dom'
import useAuthStore from '../modules/auth/store/auth.store'

export default function AuthLayout() {
  const token = useAuthStore((state) => state.token)

  if (token) return <Navigate to="/dashboard" replace />

  return (
    <div className="auth-layout">
      <Outlet />
    </div>
  )
}
