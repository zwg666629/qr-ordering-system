<template>
  <div class="order-detail">
    <!-- 导航栏 -->
    <van-nav-bar 
      title="订单详情" 
      left-text="返回" 
      left-arrow 
      @click-left="goBack"
    />
    
    <div v-if="order" class="detail-content">
      <!-- 订单状态 -->
      <div class="status-card">
        <div class="status-icon" :class="getStatusClass(order.status)">
          <van-icon :name="getStatusIcon(order.status)" />
        </div>
        <div class="status-info">
          <h3>{{ getStatusText(order.status) }}</h3>
          <p v-if="order.status === 2">请耐心等待，我们正在为您制作美食</p>
          <p v-else-if="order.status === 3">您的订单正在制作中</p>
          <p v-else-if="order.status === 4">订单已完成，请享用美食</p>
        </div>
      </div>
      
      <!-- 订单信息 -->
      <div class="order-info">
        <div class="info-row">
          <span class="label">订单号：</span>
          <span>{{ order.orderNumber }}</span>
        </div>
        <div class="info-row">
          <span class="label">桌号：</span>
          <span>{{ order.tableNumber || '未指定' }}</span>
        </div>
        <div class="info-row">
          <span class="label">下单时间：</span>
          <span>{{ formatDateTime(order.createTime) }}</span>
        </div>
        <div class="info-row" v-if="order.paymentTime">
          <span class="label">支付时间：</span>
          <span>{{ formatDateTime(order.paymentTime) }}</span>
        </div>
      </div>
      
      <!-- 商品详情 -->
      <div class="order-dishes">
        <h4>订单商品</h4>
        <div class="dish-list">
          <div 
            class="dish-item" 
            v-for="item in order.items" 
            :key="item.id"
          >
            <img 
              :src="item.image || '/src/assets/dish-placeholder.svg'" 
              :alt="item.dishName"
            />
            <div class="dish-info">
              <h5>{{ item.dishName }}</h5>
              <p class="price">¥{{ item.dishPrice }}</p>
            </div>
            <div class="quantity">
              x{{ item.quantity }}
            </div>
            <div class="subtotal">
              ¥{{ item.subtotal }}
            </div>
          </div>
        </div>
      </div>
      
      <!-- 费用明细 -->
      <div class="cost-detail">
        <div class="cost-row">
          <span>商品总计：</span>
          <span>¥{{ order.totalAmount }}</span>
        </div>
        <!-- 可以添加其他费用项目 -->
      </div>
      
      <!-- 总计 -->
      <div class="total-amount">
        <span>实付金额：</span>
        <span class="amount">¥{{ order.totalAmount }}</span>
      </div>
      
      <!-- 备注 -->
      <div class="remark" v-if="order.remark">
        <h4>备注</h4>
        <p>{{ order.remark }}</p>
      </div>
    </div>
    
    <!-- 加载中 -->
    <van-loading v-else class="loading" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { orderApi } from '@/api'

const route = useRoute()
const router = useRouter()
const order = ref(null)

// 智能返回逻辑
const goBack = () => {
  // 获取当前路由的来源信息
  const fromQuery = route.query.from
  
  // 支付完成或订单确认后的详情页，返回首页
  if (fromQuery === 'payment' || fromQuery === 'order-confirm') {
    router.push('/')
    return
  }
  
  // 如果是从订单列表或个人中心进入，使用浏览器返回
  if (fromQuery === 'order' || fromQuery === 'profile') {
    // 使用浏览器的历史返回，避免添加新的历史记录
    router.go(-1)
    return
  }
  
  // 尝试使用浏览器历史记录
  if (window.history.length > 1) {
    // 检查document.referrer
    const referrer = document.referrer
    if (referrer) {
      // 如果来自支付或订单确认页面，返回首页
      if (referrer.includes('/order-confirm')) {
        router.push('/')
        return
      }
      
      // 如果来自订单页面或个人中心，使用浏览器返回
      if (referrer.includes('/order') || referrer.includes('/profile')) {
        router.go(-1)
        return
      }
    }
    
    // 如果无法确定来源，使用浏览器返回
    router.go(-1)
    return
  }
  
  // 默认返回首页（只有在没有历史记录时）
  router.push('/')
}

// 获取订单详情
const getOrderDetail = async () => {
  try {
    const orderId = route.params.id
    const data = await orderApi.getOrderDetail(orderId)
    order.value = data
  } catch (error) {
    console.error('获取订单详情失败:', error)
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

// 获取状态图标
const getStatusIcon = (status) => {
  const iconMap = {
    1: 'clock-o',
    2: 'success',
    3: 'fire-o',
    4: 'checked',
    5: 'cross'
  }
  return iconMap[status] || 'info-o'
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

// 格式化日期时间
const formatDateTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
}

onMounted(() => {
  getOrderDetail()
})
</script>

<style scoped>
.order-detail {
  background-color: #f5f5f5;
  min-height: 100vh;
}

.detail-content {
  padding: 10px;
}

.status-card {
  background: white;
  border-radius: 10px;
  padding: 20px;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
}

.status-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  font-size: 24px;
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

.status-info h3 {
  font-size: 18px;
  color: #333;
  margin-bottom: 5px;
}

.status-info p {
  font-size: 14px;
  color: #666;
}

.order-info,
.order-dishes,
.cost-detail,
.remark {
  background: white;
  border-radius: 10px;
  padding: 15px;
  margin-bottom: 10px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 14px;
}

.info-row:last-child {
  margin-bottom: 0;
}

.label {
  color: #666;
}

.order-dishes h4,
.remark h4 {
  font-size: 16px;
  color: #333;
  margin-bottom: 15px;
}

.dish-item {
  display: flex;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #eee;
}

.dish-item:last-child {
  border-bottom: none;
}

.dish-item img {
  width: 60px;
  height: 60px;
  border-radius: 6px;
  object-fit: cover;
  margin-right: 15px;
}

.dish-info {
  flex: 1;
}

.dish-info h5 {
  font-size: 14px;
  color: #333;
  margin-bottom: 5px;
}

.dish-info .price {
  font-size: 12px;
  color: #666;
}

.quantity {
  font-size: 14px;
  color: #666;
  margin-right: 15px;
}

.subtotal {
  font-size: 14px;
  font-weight: bold;
  color: #ff6b35;
}

.cost-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 14px;
}

.cost-row:last-child {
  margin-bottom: 0;
}

.total-amount {
  background: white;
  border-radius: 10px;
  padding: 15px;
  margin-bottom: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  font-weight: bold;
}

.total-amount .amount {
  color: #ff6b35;
  font-size: 18px;
}

.remark p {
  color: #666;
  line-height: 1.5;
}

.loading {
  display: flex;
  justify-content: center;
  padding: 100px 0;
}
</style>