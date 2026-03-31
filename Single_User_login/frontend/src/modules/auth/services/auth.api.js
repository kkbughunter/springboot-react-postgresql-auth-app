import axiosInstance from '../../../lib/axios'

export const registerApi = (data) => axiosInstance.post('/auth/register', data)
export const loginApi    = (data) => axiosInstance.post('/auth/login',    data)
export const logoutApi   = ()     => axiosInstance.post('/auth/logout')
