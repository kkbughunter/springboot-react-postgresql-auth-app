import { Navigate } from 'react-router-dom'
import useAuthStore from '../modules/auth/store/auth.store'

export default function RequireAdmin({ children }) {
  const user = useAuthStore((s) => s.user)
  const isAdmin = user?.roles?.includes('ADMIN')

  if (!isAdmin) return <Navigate to="/dashboard" replace />

  return children
}

