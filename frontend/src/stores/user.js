import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))

  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  const nickname = computed(() => userInfo.value?.nickname || '未登录')
  const avatar = computed(() => userInfo.value?.avatar || '')
  const phone = computed(() => userInfo.value?.phone || '')

  // 登录
  const login = (loginData) => {
    token.value = loginData.token
    userInfo.value = {
      userId: loginData.userId,
      phone: loginData.phone,
      nickname: loginData.nickname,
      avatar: loginData.avatar
    }

    // 持久化存储
    localStorage.setItem('token', token.value)
    localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
  }

  // 登出
  const logout = () => {
    token.value = ''
    userInfo.value = null

    // 清除存储
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  // 更新用户信息
  const updateUserInfo = (newUserInfo) => {
    userInfo.value = { ...userInfo.value, ...newUserInfo }
    localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
  }

  return {
    // 状态
    token,
    userInfo,

    // 计算属性
    isLoggedIn,
    nickname,
    avatar,
    phone,

    // 方法
    login,
    logout,
    updateUserInfo
  }
})