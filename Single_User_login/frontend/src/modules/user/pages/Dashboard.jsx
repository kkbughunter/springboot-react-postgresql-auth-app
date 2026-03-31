import { useState } from 'react'
import useAuthStore from '../../auth/store/auth.store'
import { useUser } from '../hooks/useUser'

export default function Dashboard() {
  const { user, updateUser } = useAuthStore()
  const { updateProfile, loading, error } = useUser()

  const [editing, setEditing]     = useState(false)
  const [successMsg, setSuccess]  = useState('')
  const [form, setForm]           = useState({
    fullName: user?.fullName || '',
    gender:   user?.gender   || '',
  })

  const handleChange = (e) =>
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }))

  const handleSubmit = async (e) => {
    e.preventDefault()
    const updated = await updateProfile(form)
    if (updated) {
      updateUser({ fullName: updated.fullName, gender: updated.gender })
      setEditing(false)
      setSuccess('Profile updated successfully!')
      setTimeout(() => setSuccess(''), 3000)
    }
  }

  const handleCancel = () => {
    setForm({ fullName: user?.fullName || '', gender: user?.gender || '' })
    setEditing(false)
  }

  return (
    <div className="dashboard">
      <h1 className="dashboard-title">Welcome, {user?.fullName}!</h1>

      {successMsg && <div className="alert alert-success">{successMsg}</div>}

      <div className="user-card">
        <div className="card-header">
          <h3>Your Profile</h3>
          {!editing && (
            <button className="btn-edit" onClick={() => setEditing(true)}>
              Edit
            </button>
          )}
        </div>

        {editing ? (
          <form onSubmit={handleSubmit} className="edit-form">
            {error && <div className="alert alert-error">{error}</div>}

            <div className="form-group">
              <label htmlFor="fullName">Full Name</label>
              <input
                id="fullName"
                name="fullName"
                value={form.fullName}
                onChange={handleChange}
                required
              />
            </div>

            <div className="form-group">
              <label htmlFor="gender">Gender</label>
              <select
                id="gender"
                name="gender"
                value={form.gender}
                onChange={handleChange}
              >
                <option value="">Prefer not to say</option>
                <option value="Male">Male</option>
                <option value="Female">Female</option>
                <option value="Other">Other</option>
              </select>
            </div>

            <div className="edit-actions">
              <button type="submit" className="btn-primary" disabled={loading}>
                {loading ? 'Saving…' : 'Save Changes'}
              </button>
              <button type="button" className="btn-secondary" onClick={handleCancel}>
                Cancel
              </button>
            </div>
          </form>
        ) : (
          <>
            <div className="user-field">
              <span className="field-label">Full Name</span>
              <span className="field-value">{user?.fullName}</span>
            </div>
            <div className="user-field">
              <span className="field-label">Email</span>
              <span className="field-value">{user?.email}</span>
            </div>
            <div className="user-field">
              <span className="field-label">Gender</span>
              <span className="field-value">{user?.gender || 'Not specified'}</span>
            </div>
          </>
        )}
      </div>
    </div>
  )
}
