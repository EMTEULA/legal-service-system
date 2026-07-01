import request from './request'

export const authApi = {
  login: data => request.post('/login', data),
  register: data => request.post('/register', data)
}
export const categoryApi = {
  list: params => request.get('/category/list', { params }),
  all: () => request.get('/category/all'),
  add: data => request.post('/category/add', data),
  update: data => request.put('/category/update', data),
  remove: id => request.delete(`/category/delete/${id}`)
}
export const lawyerApi = {
  list: params => request.get('/lawyer/list', { params }),
  available: params => request.get('/lawyer/available', { params }),
  profile: () => request.get('/lawyer/profile'),
  add: data => request.post('/lawyer/add', data),
  update: data => request.put('/lawyer/update', data),
  remove: id => request.delete(`/lawyer/delete/${id}`)
}
export const scheduleApi = {
  list: params => request.get('/schedule/list', { params }),
  add: data => request.post('/schedule/add', data),
  update: data => request.put('/schedule/update', data),
  remove: id => request.delete(`/schedule/delete/${id}`)
}
export const appointmentApi = {
  list: params => request.get('/appointment/list', { params }),
  add: data => request.post('/appointment/add', data),
  audit: (id, data) => request.put(`/appointment/audit/${id}`, data),
  cancel: id => request.put(`/appointment/cancel/${id}`),
  complete: id => request.put(`/appointment/complete/${id}`)
}
export const orderApi = {
  list: params => request.get('/order/list', { params }),
  add: data => request.post('/order/add', data),
  audit: (id, data) => request.put(`/order/audit/${id}`, data)
}
export const applyApi = {
  list: params => request.get('/lawyerApply/list', { params }),
  submit: data => request.post('/lawyerApply/submit', data),
  audit: (id, data) => request.put(`/lawyerApply/audit/${id}`, data)
}
export const statsApi = {
  all: params => request.get('/stats/all', { params })
}
