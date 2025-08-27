<template>
  <div class="order">
    <!-- 导航栏 -->
    <van-nav-bar 
      title="我的订单" 
      left-text="返回" 
      left-arrow 
      @click-left="$router.go(-1)"
    />
    
    <!-- 订单列表 -->
    <div v-if="orders.length > 0" class="order-list">
      <div 
        class="order-item" 
        v-for="order in orders" 
        :key="order.id"
        @click="goToOrderDetail(order.id)"
      >
        <div class="order-header">
          <span class="order-number">订单号：{{ order.orderNumber }}</span>
          <span class="order-status" :class="getStatusClass(order.status)">
            {{ getStatusText(order.status) }}
          </span>
        </div>
        
        <div class="order-content">
          <div class="order-dishes">
            <div 
              class="dish-item" 
              v-for="item in order.items?.slice(0, 3)" 
              :key="item.id"
            >
              <img 
                :src="item.image || '/src/assets/dish-placeholder.svg'" 
                :alt="item.dishName"
              />
              <span>{{ item.dishName }} x{{ item.quantity }}</span>
            </div>
            <span v-if="order.items?.length > 3" class="more-dishes">
              等{{ order.items.length }}件商品
            </span>
          </div>
        </div>
        
        <div class="order-footer">
          <span class="order-time">{{ formatTime(order.createTime) }}</span>
          <span class="order-amount">总计：¥{{ order.totalAmount }}</span>
        </div>
      </div>
    </div>
    
    <!-- 空状态 -->
    <div v-else class="empty-order">
      <van-empty description="暂无订单">
        <van-button 
          type="primary" 
          size="small" 
          @click="$router.push('/menu')"
        >
          去点餐
        </van-button>
      </van-empty>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { orderApi } from '@/api'

const router = useRouter()
const orders = ref([])

// 获取订单列表
const getOrders = async () => {
  try {
    const data = await orderApi.getOrderList()
    orders.value = data
  } catch (error) {
    console.error('获取订单失败:', error)
  }
}

// 获取状态文字
const getStatusText = (status) => {
  const statusMap = {
    1: '待支付',
    2: '已支付',
    3: '制作中',
    4: '已完成',
    5: '已取消'
  }
  return statusMap[status] || '未知状态'
}

// 获取状态样式
const getStatusClass = (status) => {
  const classMap = {
    1: 'status-pending',
    2: 'status-paid',
    3: 'status-cooking',
    4: 'status-completed',
    5: 'status-cancelled'
  }
  return classMap[status] || ''
}

// 格式化时间
const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return `${date.getMonth() + 1}-${date.getDate()} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
}

// 跳转到订单详情
const goToOrderDetail = (orderId) => {
  router.push({
    path: `/order-detail/${orderId}`,
    query: { from: 'order' }
  })
}

onMounted(() => {
  getOrders()
})
</script>

<style scoped>
.order {
  background-color: #f5f5f5;
  min-height: 100vh;
}

.order-list {
  padding: 10px;
}

.order-item {
  background: white;
  border-radius: 10px;
  padding: 15px;
  margin-bottom: 10px;
  cursor: pointer;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.order-number {
  font-size: 14px;
  color: #666;
}

.order-status {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.status-pending {
  background: #fff3cd;
  color: #856404;
}

.status-paid {
  background: #d4edda;
  color: #155724;
}

.status-cooking {
  background: #cce5ff;
  color: #004085;
}

.status-completed {
  background: #d1ecf1;
  color: #0c5460;
}

.status-cancelled {
  background: #f8d7da;
  color: #721c24;
}

.order-dishes {
  margin-bottom: 15px;
}

.dish-item {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.dish-item img {
  width: 40px;
  height: 40px;
  border-radius: 4px;
  object-fit: cover;
  margin-right: 10px;
}

.dish-item span {
  font-size: 14px;
  color: #333;
}

.more-dishes {
  font-size: 12px;
  color: #999;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15px;
  border-top: 1px solid #eee;
}

.order-time {
  font-size: 12px;
  color: #999;
}

.order-amount {
  font-size: 16px;
  font-weight: bold;
  color: #ff6b35;
}

.empty-order {
  padding-top: 100px;
}
</style>