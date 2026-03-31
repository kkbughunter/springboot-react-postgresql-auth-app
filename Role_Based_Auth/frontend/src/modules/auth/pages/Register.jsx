import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../hooks/useAuth'
import useAuthStore from '../store/auth.store'

export default function Register() {
  const [form, setForm] = useState({
    orgName: '',
    orgCode: '',
    fullName: '',
    email: '',
    phone: '',
    password: '',
  })
  const { register, loading, error } = useAuth()
  const navigate = useNavigate()

  const handleChange = (e) => {
    const { name, value } = e.target
    const nextValue = name === 'orgCode' ? value.toLowerCase() : value
    setForm((prev) => ({ ...prev, [name]: nextValue }))
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    const ok = await register(form)
    if (ok) {
      const user = useAuthStore.getState().user
      const isAdmin = user?.roles?.includes('ADMIN')
      navigate(isAdmin ? '/admin/users' : '/dashboard')
    }
  }

  return (
    <div className="auth-card">
      <h2 className="auth-title">Create Account</h2>
      <p className="auth-subtitle">Create your organization and first admin account.</p>

      <form onSubmit={handleSubmit} className="auth-form">
        {error && <div className="alert alert-error">{error}</div>}

        <div className="form-group">
          <label htmlFor="orgName">Organization Name</label>
          <input
            id="orgName"
            type="text"
            name="orgName"
            value={form.orgName}
            onChange={handleChange}
            placeholder="Acme Corporation"
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="orgCode">Organization Short Name</label>
          <input
            id="orgCode"
            type="text"
            name="orgCode"
            value={form.orgCode}
            onChange={handleChange}
            placeholder="acme-corp"
            autoCapitalize="none"
            pattern="^[a-z0-9_-]+$"
            title="Lowercase letters, numbers, hyphens or underscores only"
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="fullName">Full Name</label>
          <input
            id="fullName"
            type="text"
            name="fullName"
            value={form.fullName}
            onChange={handleChange}
            placeholder="karthikeyan"
            required
          />
        </div>

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
          <label htmlFor="phone">Phone (optional)</label>
          <input
            id="phone"
            type="tel"
            name="phone"
            value={form.phone}
            onChange={handleChange}
            placeholder="+1 555 0100"
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
            placeholder="Min. 8 characters"
            minLength={8}
            required
          />
        </div>

        <button type="submit" className="btn-primary" disabled={loading}>
          {loading ? 'Creating account…' : 'Create Account'}
        </button>
      </form>

      <p className="auth-footer">
        Already have an account?{' '}
        <Link to="/login">Sign in</Link>
      </p>
    </div>
  )
}
