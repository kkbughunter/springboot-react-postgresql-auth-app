import axiosInstance from '../../../lib/axios'

export const createUserApi = (data) => axiosInstance.post('/admin/users', data)
export const listUsersApi = () => axiosInstance.get('/admin/users')
export const getUserApi = (userId) => axiosInstance.get(`/admin/users/${userId}`)
export const updateUserApi = (userId, data) =>
  axiosInstance.put(`/admin/users/${userId}`, data)
export const deleteUserApi = (userId) =>
  axiosInstance.delete(`/admin/users/${userId}`)

