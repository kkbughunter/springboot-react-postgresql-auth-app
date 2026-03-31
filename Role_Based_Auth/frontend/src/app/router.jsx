import { createBrowserRouter, Navigate } from 'react-router-dom'
import AuthLayout from '../layouts/AuthLayout'
import MainLayout from '../layouts/MainLayout'
import Login from '../modules/auth/pages/Login'
import Register from '../modules/auth/pages/Register'
import Dashboard from '../modules/user/pages/Dashboard'
import AdminUsers from '../modules/admin/pages/AdminUsers'
import RequireAdmin from '../routes/RequireAdmin'

const router = createBrowserRouter([
  {
    path: '/',
    element: <Navigate to="/login" replace />,
  },
  {
    element: <AuthLayout />,
    children: [
      { path: '/login', element: <Login /> },
      { path: '/register', element: <Register /> },
    ],
  },
  {
    element: <MainLayout />,
    children: [
      { path: '/dashboard', element: <Dashboard /> },
      {
        path: '/admin/users',
        element: (
          <RequireAdmin>
            <AdminUsers />
          </RequireAdmin>
        ),
      },
    ],
  },
])

export default router
