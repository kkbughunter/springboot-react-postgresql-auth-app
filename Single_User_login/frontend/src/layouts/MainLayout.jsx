import { Outlet, Navigate, useNavigate } from 'react-router-dom'
import { logoutApi } from '../modules/auth/services/auth.api'
import useAuthStore from '../modules/auth/store/auth.store'

export default function MainLayout() {
  const { isAuthenticated, isLoading, user, logout } = useAuthStore()
  const navigate = useNavigate()

  if (isLoading) return <div className="page-loading">Checking session…</div>

  if (!isAuthenticated) return <Navigate to="/login" replace />

  const handleLogout = async () => {
    try {
      await logoutApi()   // backend clears both httpOnly cookies
    } catch (_) {
      // still clear local state even if the network request fails
    }
    logout()
    navigate('/login')
  }

  return (
    <div className="main-layout">
      <header className="main-header">
        <span className="app-title">AuthApp</span>
        <div className="header-right">
          <span className="header-user">{user?.fullName}</span>
          <button onClick={handleLogout} className="btn-logout">
            Logout
          </button>
        </div>
      </header>
      <main className="main-content">
        <Outlet />
      </main>
    </div>
  )
}
