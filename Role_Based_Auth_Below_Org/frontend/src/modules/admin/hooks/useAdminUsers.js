import { useCallback, useEffect, useMemo, useState } from 'react'
import {
  createUserApi,
  deleteUserApi,
  listUsersApi,
  updateUserApi,
} from '../services/adminUsers.api'

export function useAdminUsers({ autoLoad = true } = {}) {
  const [users, setUsers] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  const fetchUsers = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      const res = await listUsersApi()
      setUsers(res.data.data || [])
      return res.data.data || []
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load users')
      return null
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    if (autoLoad) fetchUsers()
  }, [autoLoad, fetchUsers])

  const createUser = async (data) => {
    setError(null)
    try {
      const res = await createUserApi(data)
      const created = res.data.data
      setUsers((prev) => [created, ...prev])
      return created
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to create user')
      return null
    }
  }

  const updateUser = async (userId, data) => {
    setError(null)
    try {
      const res = await updateUserApi(userId, data)
      const updated = res.data.data
      setUsers((prev) => prev.map((u) => (u.userId === userId ? updated : u)))
      return updated
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to update user')
      return null
    }
  }

  const deleteUser = async (userId) => {
    setError(null)
    try {
      await deleteUserApi(userId)
      setUsers((prev) => prev.filter((u) => u.userId !== userId))
      return true
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to delete user')
      return false
    }
  }

  const stats = useMemo(() => {
    const active = users.filter((u) => u.isActive).length
    return { total: users.length, active, inactive: users.length - active }
  }, [users])

  return { users, stats, loading, error, fetchUsers, createUser, updateUser, deleteUser }
}

