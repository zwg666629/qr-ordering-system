import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { adminApi } from '@/api'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('admin_token') || '')
  const userInfo = ref(null)

  const isLoggedIn = computed(() => !!token.value)

  const login = async (username, password) => {
    try {
      const data = await adminApi.login({ username, password })
      token.value = data.token
      userInfo.value = {
        adminId: data.adminId,
        username: data.username,
        realName: data.realName,
        role: data.role
      }
      localStorage.setItem('admin_token', data.token)
      return { success: true, data }
    } catch (error) {
      return { success: false, message: error.message }
    }
  }

  const logout = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('admin_token')
  }

  const getUserInfo = async () => {
    if (!token.value) return null
    try {
      const data = await adminApi.getUserInfo()
      userInfo.value = data
      return data
    } catch (error) {
      logout()
      return null
    }
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    login,
    logout,
    getUserInfo
  }
})

