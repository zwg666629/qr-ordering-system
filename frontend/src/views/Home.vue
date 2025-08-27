<template>
  <div class="home">
    <!-- 导航栏 -->
    <van-nav-bar title="扫码点餐">
      <template #right>
        <div class="nav-user" @click="handleUserClick">
          <van-icon v-if="!userStore.isLoggedIn" name="contact" size="20" />
          <div v-else class="user-avatar">
            <van-image
              v-if="userStore.avatar"
              :src="userStore.avatar"
              round
              width="24"
              height="24"
              fit="cover"
            />
            <van-icon v-else name="contact" size="20" />
          </div>
        </div>
      </template>
    </van-nav-bar>
    
    <!-- 轮播图 -->
    <van-swipe class="swipe" :autoplay="3000" indicator-color="white">
      <van-swipe-item class="swipe-item">
        <div class="banner-content">
          <h2>欢迎光临</h2>
          <p>精选美食，优质服务</p>
        </div>
      </van-swipe-item>
      <van-swipe-item class="swipe-item">
        <div class="banner-content">
          <h2>新鲜食材</h2>
          <p>每日新鲜采购，品质保证</p>
        </div>
      </van-swipe-item>
    </van-swipe>
    
    <!-- 功能菜单 -->
    <div class="menu-grid">
      <div class="menu-item" @click="goToMenu">
        <van-icon name="apps-o" size="30" />
        <span>点餐</span>
      </div>
      <div class="menu-item" @click="goToCart">
        <div class="icon-container">
          <van-icon name="shopping-cart-o" size="30" />
          <van-badge v-if="cartStore.totalCount > 0" :content="cartStore.totalCount" />
        </div>
        <span>购物车</span>
      </div>
      <div class="menu-item" @click="goToOrder">
        <van-icon name="records" size="30" />
        <span>我的订单</span>
      </div>
      <div class="menu-item">
        <van-icon name="service-o" size="30" />
        <span>呼叫服务</span>
      </div>
    </div>
    
    <!-- 推荐菜品 -->
    <div class="recommend">
      <h3>推荐菜品</h3>
      
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
          <van-button type="primary" size="small" @click="getRecommendDishes">
            重新加载
          </van-button>
        </van-empty>
      </div>
      
      <!-- 菜品列表 -->
      <div v-else-if="recommendDishes.length > 0" class="dish-list">
        <div 
          class="dish-item" 
          v-for="dish in recommendDishes" 
          :key="dish.id"
        >
          <img :src="dish.image || '/src/assets/dish-placeholder.svg'" :alt="dish.name" />
          <div class="dish-info">
            <h4>{{ dish.name }}</h4>
            <p class="description">{{ dish.description }}</p>
            <div class="dish-footer">
              <span class="price">¥{{ dish.price }}</span>
              <van-button 
                type="primary" 
                size="small" 
                @click="addToCart(dish)"
                :loading="addingToCart === dish.id"
              >
                加入购物车
              </van-button>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 空状态 -->
      <div v-else class="empty-container">
        <van-empty description="暂无推荐菜品" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '@/stores/cart'
import { useUserStore } from '@/stores/user'
import { dishApi } from '@/api'
import { showToast } from 'vant'

const router = useRouter()
const cartStore = useCartStore()
const userStore = useUserStore()
const recommendDishes = ref([])
const loading = ref(false)
const error = ref(false)
const addingToCart = ref(null)

// 获取推荐菜品
const getRecommendDishes = async () => {
  loading.value = true
  error.value = false
  try {
    const dishes = await dishApi.getDishList()
    recommendDishes.value = dishes.slice(0, 4) // 取前4个作为推荐
  } catch (err) {
    console.error('获取推荐菜品失败:', err)
    error.value = true
    showToast('获取菜品失败，请重试')
  } finally {
    loading.value = false
  }
}

// 页面跳转
const goToMenu = () => {
  router.push('/menu')
}

const goToCart = () => {
  router.push('/cart')
}

const goToOrder = () => {
  router.push('/order')
}

// 处理用户点击
const handleUserClick = () => {
  if (userStore.isLoggedIn) {
    router.push('/profile')
  } else {
    router.push('/login')
  }
}

// 添加到购物车
const addToCart = async (dish) => {
  addingToCart.value = dish.id
  try {
    cartStore.addToCart(dish)
    showToast('已添加到购物车')
  } catch (error) {
    showToast('添加失败，请重试')
  } finally {
    // 延迟一点清除loading状态，让用户看到反馈
    setTimeout(() => {
      addingToCart.value = null
    }, 500)
  }
}

onMounted(() => {
  getRecommendDishes()
})
</script>

<style scoped>
.home {
  background-color: #f5f5f5;
  min-height: 100vh;
}

.nav-user {
  cursor: pointer;
  padding: 5px;
}

.user-avatar {
  display: flex;
  align-items: center;
  justify-content: center;
}

.swipe {
  height: 200px;
}

.swipe-item {
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.banner-content h2 {
  font-size: 24px;
  margin-bottom: 8px;
}

.banner-content p {
  font-size: 14px;
  opacity: 0.8;
}

.menu-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  padding: 20px;
  gap: 15px;
  background: white;
  margin: 10px;
  border-radius: 10px;
}

.menu-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 15px;
  position: relative;
  cursor: pointer;
}

.menu-item span {
  margin-top: 8px;
  font-size: 12px;
  color: #666;
}

.icon-container {
  position: relative;
  display: inline-block;
}

.icon-container .van-badge {
  position: absolute;
  top: -5px;
  right: -8px;
  z-index: 1;
}

.recommend {
  padding: 0 10px;
}

.recommend h3 {
  padding: 15px 0;
  color: #333;
  font-size: 16px;
}

.dish-list {
  display: grid;
  gap: 10px;
}

.dish-item {
  display: flex;
  background: white;
  border-radius: 10px;
  padding: 15px;
  align-items: center;
}

.dish-item img {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  object-fit: cover;
  margin-right: 15px;
}

.dish-info {
  flex: 1;
}

.dish-info h4 {
  font-size: 16px;
  color: #333;
  margin-bottom: 5px;
}

.dish-info .description {
  font-size: 12px;
  color: #999;
  margin-bottom: 10px;
  line-height: 1.4;
}

.dish-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.price {
  color: #ff6b35;
  font-size: 16px;
  font-weight: bold;
}

/* 加载和错误状态样式 */
.loading-container,
.error-container,
.empty-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px 20px;
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