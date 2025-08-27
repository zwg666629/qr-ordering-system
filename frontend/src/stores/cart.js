import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useCartStore = defineStore('cart', () => {
  // 从localStorage恢复购物车数据
  const items = ref(JSON.parse(localStorage.getItem('cartItems') || '[]'))

  // 计算总数量
  const totalCount = computed(() => {
    return items.value.reduce((total, item) => total + item.quantity, 0)
  })

  // 计算总金额
  const totalAmount = computed(() => {
    return items.value.reduce((total, item) => total + item.price * item.quantity, 0)
  })

  // 保存购物车到localStorage
  const saveToStorage = () => {
    localStorage.setItem('cartItems', JSON.stringify(items.value))
  }

  // 添加商品到购物车
  const addToCart = (dish) => {
    const existingItem = items.value.find(item => item.id === dish.id)
    
    if (existingItem) {
      existingItem.quantity += 1
    } else {
      items.value.push({
        id: dish.id,
        name: dish.name,
        price: dish.price,
        image: dish.image,
        quantity: 1
      })
    }
    saveToStorage()
  }

  // 从购物车移除商品
  const removeFromCart = (dishId) => {
    const index = items.value.findIndex(item => item.id === dishId)
    if (index > -1) {
      if (items.value[index].quantity > 1) {
        items.value[index].quantity -= 1
      } else {
        items.value.splice(index, 1)
      }
    }
    saveToStorage()
  }

  // 更新商品数量
  const updateQuantity = (dishId, quantity) => {
    const item = items.value.find(item => item.id === dishId)
    if (item) {
      if (quantity <= 0) {
        removeFromCart(dishId)
      } else {
        item.quantity = quantity
        saveToStorage()
      }
    }
  }

  // 清空购物车
  const clearCart = () => {
    items.value = []
    saveToStorage()
  }

  // 获取商品数量
  const getItemQuantity = (dishId) => {
    const item = items.value.find(item => item.id === dishId)
    return item ? item.quantity : 0
  }

  return {
    items,
    totalCount,
    totalAmount,
    addToCart,
    removeFromCart,
    updateQuantity,
    clearCart,
    getItemQuantity
  }
})