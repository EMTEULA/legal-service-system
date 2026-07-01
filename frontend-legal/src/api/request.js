import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({ baseURL: '/api', timeout: 12000 })

request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

request.interceptors.response.use(response => {
  const result = response.data
  if (result.code !== 200) {
    ElMessage.error(result.message || '操作失败')
    return Promise.reject(new Error(result.message))
  }
  return result
}, error => {
  const message = error.response?.data?.message || error.message || '网络连接失败'
  ElMessage.error(message)
  if (error.response?.status === 401) {
    localStorage.clear()
    location.href = '/login'
  }
  return Promise.reject(error)
})

export default request
