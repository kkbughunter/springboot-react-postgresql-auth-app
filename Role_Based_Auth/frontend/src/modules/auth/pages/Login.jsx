import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../hooks/useAuth'
import useAuthStore from '../store/auth.store'

export default function Login() {
  const [form, setForm] = useState({ email: '', password: '' })
  const { login, loading, error } = useAuth()
  const navigate = useNavigate()

  const handleChange = (e) => {
    const { name, value } = e.target
    setForm((prev) => ({ ...prev, [name]: value }))
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    const ok = await login(form)
    if (ok) {
      const user = useAuthStore.getState().user
      const isAdmin = user?.roles?.includes('ADMIN')
      navigate(isAdmin ? '/admin/users' : '/dashboard')
    }
  }

  return (
    <div className="auth-card">
      <h2 className="auth-title">Sign In</h2>
      <p className="auth-subtitle">Enter your credentials to continue.</p>

      <form onSubmit={handleSubmit} className="auth-form">
        {error && <div className="alert alert-error">{error}</div>}

        <div className="form-group">
          <label htmlFor="email">Email</label>
          <input
            id="email"
            type="email"
            name="email"
            value={form.email}
            onChange={handleChange}
            placeholder="you@example.com"
            autoCapitalize="none"
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="password">Password</label>
          <input
            id="password"
            type="password"
            name="password"
            value={form.password}
            onChange={handleChange}
            placeholder="••••••••"
            required
          />
        </div>

        <button type="submit" className="btn-primary" disabled={loading}>
          {loading ? 'Signing in…' : 'Sign In'}
        </button>
      </form>

      <p className="auth-footer">
        Don&apos;t have an account?{' '}
        <Link to="/register">Register here</Link>
      </p>
    </div>
  )
}
