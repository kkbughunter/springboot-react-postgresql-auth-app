import { useEffect } from 'react'
import { RouterProvider } from 'react-router-dom'
import router from './router'
import useAuthStore from '../modules/auth/store/auth.store'
import { getProfileApi } from '../modules/user/services/user.api'

export default function Providers() {
  const { setUser, logout } = useAuthStore()

  useEffect(() => {
    /**
     * On every page load we ask the backend "who am I?" using the httpOnly
     * accessToken cookie. The axios interceptor will transparently refresh the
     * access token if it has expired before this call resolves.
     *
     * Success  → populate the in-memory store (isAuthenticated = true)
     * Failure  → both cookies are invalid/missing; clear store (isAuthenticated = false)
     */
    getProfileApi()
      .then((res) => setUser(res.data.data))
      .catch(() => logout())
  }, [])

  return <RouterProvider router={router} />
}
