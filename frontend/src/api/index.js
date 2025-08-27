import axios from 'axios'
import { showToast } from 'vant'

// 创建axios实例
const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器
api.interceptors.request.use(
  config => {
    // 添加token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
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
    const { data } = response
    if (data.code === 200) {
      return data.data
    } else {
      showToast(data.message || '请求失败')
      return Promise.reject(new Error(data.message || '请求失败'))
    }
  },
  error => {
    if (error.response?.status === 401) {
      // 清除token并跳转到登录页
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      showToast('登录已过期，请重新登录')
      // 这里可以跳转到登录页，但需要在组件中处理
      window.location.href = '/login'
    } else {
      showToast(error.message || '网络错误')
    }
    return Promise.reject(error)
  }
)

// 菜品相关API
export const dishApi = {
  // 获取菜品分类
  getCategories() {
    return api.get('/dish/categories')
  },
  
  // 获取菜品列表
  getDishList(categoryId) {
    return api.get('/dish/list', { params: { categoryId } })
  },
  
  // 获取菜品详情
  getDishDetail(id) {
    return api.get(`/dish/${id}`)
  }
}

// 订单相关API
export const orderApi = {
  // 创建订单
  createOrder(orderData) {
    return api.post('/order/create', orderData)
  },

  // 获取订单列表
  getOrderList(tableId) {
    return api.get('/order/list', { params: { tableId } })
  },

  // 获取订单详情
  getOrderDetail(id) {
    return api.get(`/order/${id}`)
  },

  // 取消订单
  cancelOrder(id) {
    return api.post(`/order/${id}/cancel`)
  },

  // 支付订单
  payOrder(id, paymentMethod) {
    return api.post(`/order/${id}/pay`, null, { params: { paymentMethod } })
  }
}

// 用户相关API
export const userApi = {
  // 发送短信验证码
  sendSms(data) {
    return api.post('/user/sms/send', data)
  },

  // 用户注册
  register(data) {
    return api.post('/user/register', data)
  },

  // 用户登录
  login(data) {
    return api.post('/user/login', data)
  },

  // 获取用户信息
  getUserInfo() {
    return api.get('/user/info')
  },

  // 更新用户信息
  updateUserInfo(data) {
    return api.put('/user/info', data)
  },

  // 修改密码
  changePassword(oldPassword, newPassword) {
    return api.put('/user/password', null, {
      params: { oldPassword, newPassword }
    })
  }
}

// 文件上传API
export const fileApi = {
  // 上传头像
  uploadAvatar(file) {
    const formData = new FormData()
    formData.append('file', file)
    return api.post('/file/upload/avatar', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  }
}

// 餐桌相关API
export const tableApi = {
  // 获取所有餐桌
  getTableList() {
    return api.get('/table/list')
  },
  
  // 根据ID获取餐桌信息
  getTableById(id) {
    return api.get(`/table/${id}`)
  },
  
  // 根据餐桌号获取餐桌信息
  getTableByNumber(tableNumber) {
    return api.get(`/table/number/${tableNumber}`)
  }
}

export default api