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
      const { userId, orgId, orgCode, email, fullName, gender, phone, roles, isActive } = res.data.data
      setUser({
        userId,
        orgId,
        orgCode,
        email,
        fullName,
        gender,
        phone,
        roles,
        isActive: isActive ?? true,
      })
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
      const { userId, orgId, orgCode, email, fullName, gender, phone, roles, isActive } = res.data.data
      setUser({
        userId,
        orgId,
        orgCode,
        email,
        fullName,
        gender,
        phone,
        roles,
        isActive: isActive ?? true,
      })
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
      await logoutApi()
    } catch {
      // ignore network errors — still clear local state
    } finally {
      logout()
    }
  }

  return { login, register, logoutUser, loading, error }
}
