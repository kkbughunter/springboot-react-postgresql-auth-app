import axiosInstance from '../../../lib/axios'

export const registerApi = (data) => axiosInstance.post('/auth/register', data)

export const loginApi = (data) => axiosInstance.post('/auth/login', data)
