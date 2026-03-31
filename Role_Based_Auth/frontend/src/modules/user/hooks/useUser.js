import { useState } from 'react'
import { updateProfileApi } from '../services/user.api'

export function useUser() {
  const [loading, setLoading] = useState(false)
  const [error, setError]     = useState(null)

  const updateProfile = async (data) => {
    setLoading(true)
    setError(null)
    try {
      const res = await updateProfileApi(data)
      return res.data.data
    } catch (err) {
      setError(err.response?.data?.message || 'Update failed')
      return null
    } finally {
      setLoading(false)
    }
  }

  return { updateProfile, loading, error }
}
