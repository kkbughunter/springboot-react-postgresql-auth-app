import { create } from 'zustand'

/**
 * Pure in-memory store — nothing is written to localStorage or sessionStorage.
 * Auth state is derived exclusively from the httpOnly cookies managed by the backend.
 * On every page load, providers.jsx calls GET /user/profile to rehydrate this store.
 */
const useAuthStore = create((set) => ({
  user: null,             // { userId, email, fullName, gender }
  isAuthenticated: false,
  isLoading: true,        // true while the initial /user/profile check is in flight

  setUser: (user) => set({ user, isAuthenticated: true, isLoading: false }),

  updateUser: (updates) =>
    set((state) => ({ user: { ...state.user, ...updates } })),

  logout: () => set({ user: null, isAuthenticated: false, isLoading: false }),

  setLoading: (isLoading) => set({ isLoading }),
}))

export default useAuthStore
