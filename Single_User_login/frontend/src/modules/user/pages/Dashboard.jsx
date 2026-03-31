import useAuthStore from '../../auth/store/auth.store'

export default function Dashboard() {
  const user = useAuthStore((state) => state.user)

  return (
    <div className="dashboard">
      <h1 className="dashboard-title">Welcome, {user?.fullName}!</h1>
      <div className="user-card">
        <h3>Your Profile</h3>
        <div className="user-field">
          <span className="field-label">Full Name</span>
          <span className="field-value">{user?.fullName}</span>
        </div>
        <div className="user-field">
          <span className="field-label">Email</span>
          <span className="field-value">{user?.email}</span>
        </div>
      </div>
    </div>
  )
}
