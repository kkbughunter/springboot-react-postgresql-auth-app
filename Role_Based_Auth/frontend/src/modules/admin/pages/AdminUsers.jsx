import { useMemo, useState } from 'react'
import useAuthStore from '../../auth/store/auth.store'
import { useAdminUsers } from '../hooks/useAdminUsers'

const ROLE_OPTIONS = [
  { label: 'User', value: 2 },
  { label: 'Admin', value: 1 },
]

export default function AdminUsers() {
  const currentUserId = useAuthStore((s) => s.user?.userId)
  const { users, stats, loading, error, createUser, updateUser, deleteUser } =
    useAdminUsers()

  const [createForm, setCreateForm] = useState({
    fullName: '',
    email: '',
    password: '',
    gender: '',
    phone: '',
    roleId: 2,
  })

  const [editingUserId, setEditingUserId] = useState(null)
  const editingUser = useMemo(
    () => users.find((u) => u.userId === editingUserId) || null,
    [editingUserId, users]
  )
  const [editForm, setEditForm] = useState({
    fullName: '',
    gender: '',
    phone: '',
    isActive: true,
  })

  const handleCreateChange = (e) => {
    const { name, value } = e.target
    setCreateForm((prev) => ({
      ...prev,
      [name]: name === 'roleId' ? Number(value) : value,
    }))
  }

  const handleEditChange = (e) => {
    const { name, value, type, checked } = e.target
    setEditForm((prev) => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value,
    }))
  }

  const startEdit = (u) => {
    setEditingUserId(u.userId)
    setEditForm({
      fullName: u.fullName || '',
      gender: u.gender || '',
      phone: u.phone || '',
      isActive: Boolean(u.isActive),
    })
  }

  const cancelEdit = () => {
    setEditingUserId(null)
    setEditForm({ fullName: '', gender: '', phone: '', isActive: true })
  }

  const submitCreate = async (e) => {
    e.preventDefault()
    const created = await createUser(createForm)
    if (!created) return
    setCreateForm({
      fullName: '',
      email: '',
      password: '',
      gender: '',
      phone: '',
      roleId: 2,
    })
  }

  const submitEdit = async (e) => {
    e.preventDefault()
    if (!editingUserId) return
    const updated = await updateUser(editingUserId, editForm)
    if (updated) cancelEdit()
  }

  const toggleActive = async (u) => {
    await updateUser(u.userId, {
      fullName: u.fullName,
      gender: u.gender || '',
      phone: u.phone || '',
      isActive: !u.isActive,
    })
  }

  const confirmDelete = async (u) => {
    const ok = window.confirm(`Delete user "${u.fullName}"? (This disables the account)`)
    if (!ok) return
    await deleteUser(u.userId)
    if (editingUserId === u.userId) cancelEdit()
  }

  return (
    <div className="admin-page">
      <div className="admin-header">
        <div>
          <h1 className="page-title">Admin Users</h1>
          <div className="admin-subtitle">
            {stats.total} total • {stats.active} active • {stats.inactive} inactive
          </div>
        </div>
      </div>

      {error && <div className="alert alert-error">{error}</div>}

      <div className="admin-grid">
        <section className="panel">
          <div className="panel-title">Create User</div>
          <form onSubmit={submitCreate} className="panel-form">
            <div className="form-row">
              <div className="form-group">
                <label htmlFor="c_fullName">Full Name</label>
                <input
                  id="c_fullName"
                  name="fullName"
                  value={createForm.fullName}
                  onChange={handleCreateChange}
                  required
                />
              </div>
              <div className="form-group">
                <label htmlFor="c_email">Email</label>
                <input
                  id="c_email"
                  type="email"
                  name="email"
                  value={createForm.email}
                  onChange={handleCreateChange}
                  autoCapitalize="none"
                  required
                />
              </div>
            </div>

            <div className="form-row">
              <div className="form-group">
                <label htmlFor="c_password">Password</label>
                <input
                  id="c_password"
                  type="password"
                  name="password"
                  value={createForm.password}
                  onChange={handleCreateChange}
                  minLength={8}
                  required
                />
              </div>
              <div className="form-group">
                <label htmlFor="c_roleId">Role</label>
                <select
                  id="c_roleId"
                  name="roleId"
                  value={createForm.roleId}
                  onChange={handleCreateChange}
                >
                  {ROLE_OPTIONS.map((r) => (
                    <option key={r.value} value={r.value}>
                      {r.label}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            <div className="form-row">
              <div className="form-group">
                <label htmlFor="c_gender">Gender (optional)</label>
                <input
                  id="c_gender"
                  name="gender"
                  value={createForm.gender}
                  onChange={handleCreateChange}
                  placeholder="Male / Female / Other"
                />
              </div>
              <div className="form-group">
                <label htmlFor="c_phone">Phone (optional)</label>
                <input
                  id="c_phone"
                  name="phone"
                  value={createForm.phone}
                  onChange={handleCreateChange}
                  placeholder="+1 555 0100"
                />
              </div>
            </div>

            <button className="btn-primary" type="submit">
              Create
            </button>
          </form>
        </section>

        <section className="panel">
          <div className="panel-title">Users</div>
          {loading ? (
            <div className="panel-loading">Loading users…</div>
          ) : (
            <div className="table-wrap">
              <table className="admin-table">
                <thead>
                  <tr>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Roles</th>
                    <th>Status</th>
                    <th className="actions">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {users.map((u) => (
                    <tr key={u.userId} className={!u.isActive ? 'row-inactive' : ''}>
                      <td>
                        <div className="cell-primary">{u.fullName}</div>
                        <div className="cell-secondary">{u.phone || '—'}</div>
                      </td>
                      <td>{u.email}</td>
                      <td>
                        <div className="role-badges">
                          {(u.roles || []).map((role) => (
                            <span
                              key={role}
                              className={`role-badge role-${role.toLowerCase()}`}
                            >
                              {role}
                            </span>
                          ))}
                        </div>
                      </td>
                      <td>
                        <span className={u.isActive ? 'status status-active' : 'status status-inactive'}>
                          {u.isActive ? 'Active' : 'Inactive'}
                        </span>
                      </td>
                      <td className="actions">
                        <button className="btn-link" onClick={() => startEdit(u)}>
                          Edit
                        </button>
                        <button
                          className="btn-link"
                          onClick={() => toggleActive(u)}
                          disabled={u.userId === currentUserId}
                          title={u.userId === currentUserId ? 'You cannot deactivate yourself' : ''}
                        >
                          {u.isActive ? 'Deactivate' : 'Activate'}
                        </button>
                        <button
                          className="btn-link btn-danger"
                          onClick={() => confirmDelete(u)}
                          disabled={u.userId === currentUserId}
                          title={u.userId === currentUserId ? 'You cannot delete yourself' : ''}
                        >
                          Delete
                        </button>
                      </td>
                    </tr>
                  ))}
                  {users.length === 0 && (
                    <tr>
                      <td colSpan={5} className="empty">
                        No users found.
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          )}
        </section>
      </div>

      {editingUser && (
        <section className="panel panel-edit">
          <div className="panel-title">Edit User</div>
          <div className="panel-subtitle">
            {editingUser.email} • {(editingUser.roles || []).join(', ') || 'No roles'}
          </div>
          <form onSubmit={submitEdit} className="panel-form">
            <div className="form-row">
              <div className="form-group">
                <label htmlFor="e_fullName">Full Name</label>
                <input
                  id="e_fullName"
                  name="fullName"
                  value={editForm.fullName}
                  onChange={handleEditChange}
                  required
                />
              </div>
              <div className="form-group">
                <label htmlFor="e_gender">Gender</label>
                <input
                  id="e_gender"
                  name="gender"
                  value={editForm.gender}
                  onChange={handleEditChange}
                />
              </div>
            </div>

            <div className="form-row">
              <div className="form-group">
                <label htmlFor="e_phone">Phone</label>
                <input
                  id="e_phone"
                  name="phone"
                  value={editForm.phone}
                  onChange={handleEditChange}
                />
              </div>
              <div className="form-group checkbox">
                <label htmlFor="e_isActive">Active</label>
                <input
                  id="e_isActive"
                  type="checkbox"
                  name="isActive"
                  checked={editForm.isActive}
                  onChange={handleEditChange}
                />
              </div>
            </div>

            <div className="edit-actions">
              <button type="submit" className="btn-primary">
                Save
              </button>
              <button type="button" className="btn-secondary" onClick={cancelEdit}>
                Cancel
              </button>
            </div>
          </form>
        </section>
      )}
    </div>
  )
}
