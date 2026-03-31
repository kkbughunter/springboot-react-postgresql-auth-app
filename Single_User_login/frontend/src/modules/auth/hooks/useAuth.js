import { useState } from 'react'
import { loginApi, registerApi } from '../services/auth.api'
import useAuthStore from '../store/auth.store'

export function useAuth() {
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const setAuth = useAuthStore((state) => state.setAuth)
  const logout = useAuthStore((state) => state.logout)

  const login = async (credentials) => {
    setLoading(true)
    setError(null)
    try {
      const res = await loginApi(credentials)
      const { token, email, fullName } = res.data.data
      setAuth(token, { email, fullName })
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
      const { token, email, fullName } = res.data.data
      setAuth(token, { email, fullName })
      return true
    } catch (err) {
      setError(err.response?.data?.message || 'Registration failed')
      return false
    } finally {
      setLoading(false)
    }
  }

  return { login, register, logout, loading, error }
}
