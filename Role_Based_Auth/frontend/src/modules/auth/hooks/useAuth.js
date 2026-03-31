import { useState } from 'react'
import { loginApi, logoutApi, registerApi } from '../services/auth.api'
import useAuthStore from '../store/auth.store'

export function useAuth() {
  const [loading, setLoading] = useState(false)
  const [error, setError]     = useState(null)
  const setUser  = useAuthStore((state) => state.setUser)
  const logout   = useAuthStore((state) => state.logout)

  const login = async (credentials) => {
    setLoading(true)
    setError(null)
    try {
      const res = await loginApi(credentials)
      const { userId, email, fullName, gender } = res.data.data
      setUser({ userId, email, fullName, gender })
      return true
    } catch (err) {
      setError(err.response?.data?.message || 'Login failed')
      return false
    } finally {
      setLoading(false)
    }
  }

  const register = async (userData) => {
    setLoading(true)
    setError(null)
    try {
      const res = await registerApi(userData)
      const { userId, email, fullName, gender } = res.data.data
      setUser({ userId, email, fullName, gender })
      return true
    } catch (err) {
      setError(err.response?.data?.message || 'Registration failed')
      return false
    } finally {
      setLoading(false)
    }
  }

  const logoutUser = async () => {
    try {
      await logoutApi()   // backend clears httpOnly cookies
    } catch (_) {
      // ignore network errors — still clear local state
    } finally {
      logout()
    }
  }

  return { login, register, logoutUser, loading, error }
}
