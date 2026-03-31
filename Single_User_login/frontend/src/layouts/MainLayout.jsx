import { Outlet, Navigate, useNavigate } from 'react-router-dom'
import useAuthStore from '../modules/auth/store/auth.store'

export default function MainLayout() {
  const { token, user, logout } = useAuthStore()
  const navigate = useNavigate()

  if (!token) return <Navigate to="/login" replace />

  const handleLogout = () => {
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
