import { NavLink, Outlet, Navigate, useNavigate } from 'react-router-dom'
import { logoutApi } from '../modules/auth/services/auth.api'
import useAuthStore from '../modules/auth/store/auth.store'

export default function MainLayout() {
  const { isAuthenticated, isLoading, user, logout } = useAuthStore()
  const navigate = useNavigate()
  const isAdmin = user?.roles?.includes('ADMIN')

  if (isLoading) return <div className="page-loading">Checking session…</div>

  if (!isAuthenticated) return <Navigate to="/login" replace />

  const handleLogout = async () => {
    try {
      await logoutApi()   // backend clears both httpOnly cookies
    } catch {
      // still clear local state even if the network request fails
    }
    logout()
    navigate('/login')
  }

  return (
    <div className="main-layout">
      <header className="main-header">
        <div className="header-left">
          <span className="app-title">AuthApp</span>
          {user?.orgCode && <span className="org-pill">{user.orgCode}</span>}
          <nav className="main-nav">
            <NavLink to="/dashboard" className="nav-link">
              Dashboard
            </NavLink>
            {isAdmin && (
              <NavLink to="/admin/users" className="nav-link">
                Admin
              </NavLink>
            )}
          </nav>
        </div>
        <div className="header-right">
          <div className="header-userblock">
            <span className="header-user">{user?.fullName}</span>
            <div className="role-badges header-roles">
              {(user?.roles || []).map((role) => (
                <span key={role} className={`role-badge role-${role.toLowerCase()}`}>
                  {role}
                </span>
              ))}
            </div>
          </div>
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
