import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'
import { useUserStore } from '@/stores/user'

// 创建axios实例
const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器
api.interceptors.request.use(
  config => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers['Authorization'] = `Bearer ${userStore.token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  response => {
    // 如果是文件下载请求，直接返回blob数据
    if (response.config.responseType === 'blob') {
      return response.data
    }
    
    const { data } = response
    if (data.code === 200) {
      return data.data
    } else {
      ElMessage.error(data.message || '请求失败')
      return Promise.reject(new Error(data.message || '请求失败'))
    }
  },
  error => {
    if (error.response?.status === 401) {
      const userStore = useUserStore()
      userStore.logout()
      router.push('/login')
      ElMessage.error('登录已过期，请重新登录')
    } else {
      ElMessage.error(error.message || '网络错误')
    }
    return Promise.reject(error)
  }
)

// 管理员相关API
export const adminApi = {
  // 登录
  login(data) {
    return api.post('/admin/login', data)
  },
  
  // 获取用户信息
  getUserInfo() {
    return api.get('/admin/info')
  }
}

// 菜品相关API
export const dishApi = {
  // 获取菜品列表
  getDishList(params) {
    return api.get('/dish/list', { params })
  },
  
  // 获取菜品详情
  getDishDetail(id) {
    return api.get(`/dish/${id}`)
  },
  
  // 创建菜品
  createDish(data) {
    return api.post('/dish/create', data)
  },
  
  // 更新菜品
  updateDish(id, data) {
    return api.put(`/dish/${id}`, data)
  },
  
  // 删除菜品
  deleteDish(id) {
    return api.delete(`/dish/${id}`)
  },
  
  // 获取分类列表
  getCategories() {
    return api.get('/category/list')
  },
  
  // 创建分类
  createCategory(data) {
    return api.post('/category/create', data)
  },
  
  // 更新分类
  updateCategory(id, data) {
    return api.put(`/category/${id}`, data)
  },
  
  // 删除分类
  deleteCategory(id) {
    return api.delete(`/category/${id}`)
  }
}

// 订单相关API
export const orderApi = {
  // 获取订单列表
  getOrderList(params) {
    return api.get('/order/list', { params })
  },
  
  // 获取订单详情
  getOrderDetail(id) {
    return api.get(`/order/${id}`)
  },
  
  // 更新订单状态
  updateOrderStatus(id, status) {
    return api.put(`/order/${id}/status`, null, { params: { status } })
  },
  
  // 完成订单
  completeOrder(id) {
    return api.post(`/order/${id}/complete`)
  }
}

// 餐桌相关API
export const tableApi = {
  // 获取餐桌列表
  getTableList() {
    return api.get('/table/list')
  },
  
  // 创建餐桌
  createTable(data) {
    return api.post('/table/create', data)
  },
  
  // 批量创建餐桌
  batchCreateTables(params) {
    return api.post('/table/batch-create', null, { params })
  },
  
  // 更新餐桌
  updateTable(id, data) {
    return api.put(`/table/${id}`, data)
  },
  
  // 更新餐桌状态
  updateTableStatus(id, status) {
    return api.put(`/table/${id}/status`, null, { params: { status } })
  },
  
  // 删除餐桌
  deleteTable(id) {
    return api.delete(`/table/${id}`)
  },
  
  // 获取餐桌二维码
  getTableQrCode(id) {
    return api.get(`/table/${id}/qrcode`)
  }
}

// 统计相关API
export const statisticsApi = {
  // 获取今日统计
  getTodayStatistics() {
    return api.get('/statistics/today')
  },
  
  // 获取销售趋势
  getSalesTrend(params) {
    return api.get('/statistics/sales-trend', { params })
  },
  
  // 获取菜品销量排行
  getDishRanking(params) {
    return api.get('/statistics/dish-ranking', { params })
  }
}

// 用户管理相关API（管理端）
export const userManagementApi = {
  // 获取用户列表
  getUserList(params) {
    return api.get('/admin/user/list', { params })
  },
  
  // 获取用户详情
  getUserDetail(id) {
    return api.get(`/admin/user/${id}`)
  },
  
  // 更新用户状态
  updateUserStatus(id, status) {
    return api.put(`/admin/user/${id}/status`, { status })
  },

  // 获取用户统计信息
  getUserStatistics() {
    return api.get('/admin/user/statistics')
  },

  // 重置用户密码
  resetUserPassword(id) {
    return api.post(`/admin/user/${id}/reset-password`)
  }
}

// 文件管理相关API
export const fileManagementApi = {
  // 获取文件列表
  getFileList(params) {
    return api.get('/admin/file/list', { params })
  },
  
  // 上传文件
  uploadFile(file, category = 'general') {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('category', category)
    return api.post('/admin/file/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  },
  
  // 获取文件详情
  getFileDetail(id) {
    return api.get(`/admin/file/${id}`)
  },
  
  // 删除文件
  deleteFile(id) {
    return api.delete(`/admin/file/${id}`)
  },

  // 批量删除文件
  batchDeleteFiles(fileIds) {
    return api.delete('/admin/file/batch', { data: fileIds })
  },

  // 获取文件统计信息
  getFileStatistics() {
    return api.get('/admin/file/statistics')
  },

  // 下载文件
  downloadFile(id) {
    return api.get(`/admin/file/download/${id}`, {
      responseType: 'blob'
    })
  }
}

// 管理员管理相关API
export const adminManagementApi = {
  // 获取管理员列表
  getAdminList(params) {
    return api.get('/admin/admin/list', { params })
  },
  
  // 获取管理员详情
  getAdminDetail(id) {
    return api.get(`/admin/admin/${id}`)
  },
  
  // 创建管理员
  createAdmin(data) {
    return api.post('/admin/admin/create', data)
  },
  
  // 更新管理员
  updateAdmin(id, data) {
    return api.put(`/admin/admin/${id}`, data)
  },

  // 更新管理员状态
  updateAdminStatus(id, status) {
    return api.put(`/admin/admin/${id}/status`, { status })
  },
  
  // 重置管理员密码
  resetAdminPassword(id) {
    return api.post(`/admin/admin/${id}/reset-password`)
  },

  // 获取管理员统计信息
  getAdminStatistics() {
    return api.get('/admin/admin/statistics')
  }
}

export default api

