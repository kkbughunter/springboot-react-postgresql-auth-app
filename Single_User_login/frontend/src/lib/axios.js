import axios from 'axios'
import useAuthStore from '../modules/auth/store/auth.store'

const axiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api',
  headers: { 'Content-Type': 'application/json' },
  withCredentials: true,  // send / receive httpOnly cookies automatically
})

// ── Refresh-token queue ───────────────────────────────────────────────────────
let isRefreshing = false
let pendingQueue = []  // [ { resolve, reject } ]

function processQueue(error) {
  pendingQueue.forEach((p) => (error ? p.reject(error) : p.resolve()))
  pendingQueue = []
}

// ── Response interceptor: auto-refresh on 401 ────────────────────────────────
axiosInstance.interceptors.response.use(
  (response) => response,
  async (error) => {
    const original = error.config

    const is401 = error.response?.status === 401
    const isRefreshEndpoint = original.url?.includes('/auth/refresh')
    const alreadyRetried = original._retry

    if (is401 && !isRefreshEndpoint && !alreadyRetried) {
      if (isRefreshing) {
        // Queue this request until the ongoing refresh completes
        return new Promise((resolve, reject) => {
          pendingQueue.push({ resolve, reject })
        })
          .then(() => axiosInstance(original))
          .catch((err) => Promise.reject(err))
      }

      original._retry = true
      isRefreshing = true

      try {
        await axiosInstance.post('/auth/refresh')  // backend rotates cookies
        processQueue(null)
        return axiosInstance(original)             // retry original request
      } catch (refreshErr) {
        processQueue(refreshErr)
        useAuthStore.getState().logout()
        window.location.href = '/login'
        return Promise.reject(refreshErr)
      } finally {
        isRefreshing = false
      }
    }

    return Promise.reject(error)
  }
)

export default axiosInstance
