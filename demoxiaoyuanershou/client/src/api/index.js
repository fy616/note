import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

api.interceptors.response.use(
  res => res.data,
  err => {
    if (err.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      if (window.location.pathname !== '/login') {
        window.location.href = '/login'
      }
      return Promise.reject(err.response.data || { message: '未登录' })
    }
    return Promise.reject(err.response?.data || { message: '网络错误' })
  }
)

export function login(username, password) {
  return api.post('/auth/login', { username, password })
}

export function register(data) {
  return api.post('/auth/register', data)
}

export function getMe() {
  return api.get('/auth/me')
}

export function getOrders(status) {
  return api.get('/orders', { params: { status } })
}

export function createOrder(data) {
  return api.post('/orders', data)
}

export function resubmitOrder(id, data) {
  return api.put(`/orders/${id}/resubmit`, data)
}

export function takeOrder(id) {
  return api.put(`/orders/${id}/take`)
}

export function completeOrder(id) {
  return api.put(`/orders/${id}/complete`)
}

export function deleteOrder(id) {
  return api.delete(`/orders/${id}`)
}

export function getAdminUsers() {
  return api.get('/admin/users')
}

export function freezeUser(id) {
  return api.put(`/admin/users/${id}/freeze`)
}

export function unfreezeUser(id) {
  return api.put(`/admin/users/${id}/unfreeze`)
}

export function deleteUser(id) {
  return api.delete(`/admin/users/${id}`)
}

export function getReviewOrders() {
  return api.get('/admin/review')
}

export function approveOrder(id) {
  return api.put(`/admin/review/${id}/approve`)
}

export function rejectOrder(id, message) {
  return api.put(`/admin/review/${id}/reject`, { message })
}

// 分类相关
export function getCategories() {
  return api.get('/categories')
}

export function createCategory(data) {
  return api.post('/categories', data)
}

export function updateCategory(id, data) {
  return api.put(`/categories/${id}`, data)
}

export function deleteCategory(id) {
  return api.delete(`/categories/${id}`)
}

// 消息相关
export function getMessages(orderId) {
  return api.get(`/orders/${orderId}/messages`)
}

export function sendMessage(orderId, content) {
  return api.post(`/orders/${orderId}/messages`, { content })
}

export function getUnreadCount(orderId) {
  return api.get(`/orders/${orderId}/messages/unread`)
}

export function getStats() {
  return api.get('/stats')
}
