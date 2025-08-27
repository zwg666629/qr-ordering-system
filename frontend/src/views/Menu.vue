<template>
  <div class="menu">
    <!-- 导航栏 -->
    <van-nav-bar 
      :title="pageTitle" 
      left-text="返回" 
      left-arrow 
      @click-left="goBack"
    />
    
    <!-- 餐桌信息提示 -->
    <div v-if="tableNumber" class="table-info">
      <van-notice-bar 
        :text="`您正在浏览 ${tableNumber} 号餐桌菜单`"
        left-icon="info-o"
        background="#fff7ed"
        color="#ed6a0c"
      />
    </div>
    
    <!-- 分类标签 -->
    <van-tabs v-model:active="activeCategory" @change="onCategoryChange">
      <van-tab 
        v-for="category in categories" 
        :key="category.id" 
        :title="category.name"
      />
    </van-tabs>
    
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <van-loading size="24px" vertical>加载中...</van-loading>
    </div>
    
    <!-- 错误状态 -->
    <div v-else-if="error" class="error-container">
      <van-empty 
        image="error" 
        description="加载失败"
      >
        <van-button type="primary" size="small" @click="getDishes()">
          重新加载
        </van-button>
      </van-empty>
    </div>
    
    <!-- 菜品列表 -->
    <div v-else-if="dishes.length > 0" class="dish-list">
      <div 
        class="dish-item" 
        v-for="dish in dishes" 
        :key="dish.id"
      >
        <img 
          :src="dish.image || '/src/assets/dish-placeholder.svg'" 
          :alt="dish.name" 
          class="dish-image"
        />
        <div class="dish-info">
          <h3>{{ dish.name }}</h3>
          <p class="description">{{ dish.description }}</p>
          <div class="dish-footer">
            <span class="price">¥{{ dish.price }}</span>
            <div class="quantity-control">
              <van-button 
                v-show="getQuantity(dish.id) > 0"
                icon="minus" 
                size="small" 
                @click="removeFromCart(dish.id)"
              />
              <span 
                v-show="getQuantity(dish.id) > 0" 
                class="quantity"
              >
                {{ getQuantity(dish.id) }}
              </span>
              <van-button 
                icon="plus" 
                type="primary" 
                size="small" 
                @click="addToCart(dish)"
              />
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 空状态 -->
    <div v-else class="empty-container">
      <van-empty description="暂无菜品" />
    </div>
    
    <!-- 购物车悬浮按钮 -->
    <div class="cart-float" v-if="cartStore.totalCount > 0" @click="goToCart">
      <van-icon name="shopping-cart-o" />
      <van-badge :content="cartStore.totalCount" />
      <span>¥{{ cartStore.totalAmount.toFixed(2) }}</span>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useCartStore } from '@/stores/cart'
import { dishApi } from '@/api'
import { showToast } from 'vant'

const router = useRouter()
const route = useRoute()
const cartStore = useCartStore()

const categories = ref([])
const dishes = ref([])
const activeCategory = ref(0)
const loading = ref(false)
const error = ref(false)
const tableNumber = ref('')

// 页面标题
const pageTitle = computed(() => {
  return tableNumber.value ? `餐桌${tableNumber.value} - 菜单` : '菜单'
})

// 获取分类列表
const getCategories = async () => {
  try {
    const data = await dishApi.getCategories()
    categories.value = [{ id: null, name: '全部' }, ...data]
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

// 获取菜品列表
const getDishes = async (categoryId = null) => {
  loading.value = true
  error.value = false
  try {
    const data = await dishApi.getDishList(categoryId)
    dishes.value = data
  } catch (err) {
    console.error('获取菜品失败:', err)
    error.value = true
    showToast('获取菜品失败')
  } finally {
    loading.value = false
  }
}

// 分类切换
const onCategoryChange = (index) => {
  const categoryId = categories.value[index]?.id
  getDishes(categoryId)
}

// 添加到购物车
const addToCart = (dish) => {
  cartStore.addToCart(dish)
  showToast('已添加到购物车')
}

// 从购物车移除
const removeFromCart = (dishId) => {
  cartStore.removeFromCart(dishId)
}

// 获取商品数量
const getQuantity = (dishId) => {
  return cartStore.getItemQuantity(dishId)
}

// 跳转到购物车
const goToCart = () => {
  // 如果有餐桌号，带上餐桌参数
  if (tableNumber.value) {
    router.push({ 
      path: '/cart', 
      query: { table: tableNumber.value }
    })
  } else {
    router.push('/cart')
  }
}

// 智能返回逻辑
const goBack = () => {
  // 如果是扫码进入的(有餐桌号)，返回首页
  if (tableNumber.value) {
    router.push('/')
  } else {
    // 普通进入，使用浏览器返回
    router.go(-1)
  }
}

onMounted(async () => {
  // 获取URL中的餐桌号参数
  const tableParam = route.query.table
  if (tableParam) {
    tableNumber.value = tableParam
    console.log('检测到餐桌号:', tableNumber.value)
    
    // 保存到sessionStorage，避免刷新丢失
    sessionStorage.setItem('currentTable', tableNumber.value)
    
    // 显示欢迎消息
    showToast(`欢迎来到${tableNumber.value}号餐桌！`)
  } else {
    // 尝试从sessionStorage恢复
    const savedTable = sessionStorage.getItem('currentTable')
    if (savedTable) {
      tableNumber.value = savedTable
    }
  }
  
  await getCategories()
  await getDishes()
})
</script>

<style scoped>
.menu {
  background-color: #f5f5f5;
  min-height: 100vh;
  padding-bottom: 80px;
}

.table-info {
  margin-bottom: 10px;
}

.dish-list {
  padding: 10px;
}

.dish-item {
  display: flex;
  background: #ffffff;
  border-radius: 10px;
  padding: 15px;
  margin-bottom: 10px;
  align-items: center;
}

.dish-image {
  width: 100px;
  height: 100px;
  border-radius: 8px;
  object-fit: cover;
  margin-right: 15px;
}

.dish-info {
  flex: 1;
}

.dish-info h3 {
  font-size: 16px;
  color: #333;
  margin-bottom: 8px;
}

.dish-info .description {
  font-size: 13px;
  color: #666;
  margin-bottom: 15px;
  line-height: 1.4;
}

.dish-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.price {
  color: #ff6b35;
  font-size: 18px;
  font-weight: bold;
}

.quantity-control {
  display: flex;
  align-items: center;
  gap: 10px;
}

.quantity {
  font-size: 16px;
  font-weight: bold;
  min-width: 20px;
  text-align: center;
}

.cart-float {
  position: fixed;
  bottom: 20px;
  right: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 25px;
  padding: 10px 20px;
  display: flex;
  align-items: center;
  gap: 8px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
  cursor: pointer;
  z-index: 100;
}

.cart-float span {
  font-size: 14px;
  font-weight: bold;
}

/* 加载和错误状态样式 */
.loading-container,
.error-container,
.empty-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 60px 20px;
  text-align: center;
}

.loading-container {
  color: #999;
}

.error-container .van-empty {
  padding: 20px;
}

.empty-container {
  color: #999;
}
</style>