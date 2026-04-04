import axiosInstance from '../../../lib/axios'

export const getProfileApi    = ()     => axiosInstance.get('/user/profile')
export const updateProfileApi = (data) => axiosInstance.patch('/user/profile', data)
